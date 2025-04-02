import com.example.legalgpt.*
import com.example.legalgpt.models.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.concurrent.TimeUnit

// First API interface
interface LegalGptApi {
    @Multipart
    @POST("/upload")
    suspend fun analyzeDocument(
        @Part file: MultipartBody.Part
    ): Response<AnalysisResponse>
}

interface ChatApiService {
    @POST("/negotiate")
    suspend fun sendMessage(@Body request: ChatRequest): Response<ChatResponse>
}

// Second API interface
interface ComplianceApi {
    @Multipart
    @POST("/upload")
    suspend fun analyzeDocumentCompliance(
        @Part file: MultipartBody.Part
    ): Response<LegalAnalysisResponse>
}

interface ContractApi {
    @POST("/generate")
    suspend fun generateContract(@Body request: ContractRequest): Response<ContractResponse>
}

// Text Processing API
interface OCRApiService {
    @POST("/analyze-clauses")
    suspend fun processText(@Body request: OCRApiRequest): Response<OCRApiResponse>
}


object RetrofitClient {
    private const val LEGAL_BASE_URL = "https://risk-legal-brb3.onrender.com"
    private const val COMPLIANCE_BASE_URL = "https://compliance-3c92.onrender.com"
    private const val CONTRACT_BASE_URL = "https://contractpdf.onrender.com"
    private const val CHAT_BASE_URL = "https://convo-legal-mistral.onrender.com"
    private const val TEXT_PROCESSING_BASE_URL = "https://mistral-new.onrender.com"

    // Standard timeout (30 seconds)
    private const val STANDARD_TIMEOUT = 30L

    // Extended timeout for compliance API (5 minutes)
    private const val EXTENDED_TIMEOUT = 300L

    private val standardOkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(STANDARD_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(STANDARD_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(STANDARD_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    private val extendedTimeoutOkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(EXTENDED_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(EXTENDED_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(EXTENDED_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    private val legalRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(LEGAL_BASE_URL)
            .client(standardOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val complianceRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(COMPLIANCE_BASE_URL)
            .client(extendedTimeoutOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val contractRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(CONTRACT_BASE_URL)
            .client(standardOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val chatRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(CHAT_BASE_URL)
            .client(standardOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val textProcessingRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(TEXT_PROCESSING_BASE_URL)
            .client(standardOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Create API instances
    val legalGptApi: LegalGptApi by lazy {
        legalRetrofit.create(LegalGptApi::class.java)
    }

    val complianceApi: ComplianceApi by lazy {
        complianceRetrofit.create(ComplianceApi::class.java)
    }

    val contractApi: ContractApi by lazy {
        contractRetrofit.create(ContractApi::class.java)
    }

    val chatApiService: ChatApiService by lazy {
        chatRetrofit.create(ChatApiService::class.java)
    }

    val apiService: OCRApiService by lazy {
        textProcessingRetrofit.create(OCRApiService::class.java)
    }

    // Function to process text (calls the new `processText()` API)
    suspend fun processText(request: OCRApiRequest): Response<OCRApiResponse> {
        return apiService.processText(request)
    }

    suspend fun analyzeDocument(pdfBytes: ByteArray): Response<AnalysisResponse> {
        val requestBody = MultipartBody.Part.createFormData(
            "file",
            "document.pdf",
            pdfBytes.toRequestBody("application/pdf".toMediaTypeOrNull())
        )
        return legalGptApi.analyzeDocument(requestBody)
    }

    suspend fun analyzeDocumentCompliance(pdfBytes: ByteArray): Response<LegalAnalysisResponse> {
        val requestBody = MultipartBody.Part.createFormData(
            "file",
            "document.pdf",
            pdfBytes.toRequestBody("application/pdf".toMediaTypeOrNull())
        )
        return complianceApi.analyzeDocumentCompliance(requestBody)
    }

    suspend fun generateContract(contractDetails: ContractRequest): Response<ContractResponse> {
        return contractApi.generateContract(contractDetails)
    }

    suspend fun sendChatMessage(request: ChatRequest): Response<ChatResponse> {
        return chatApiService.sendMessage(request)
    }
}
