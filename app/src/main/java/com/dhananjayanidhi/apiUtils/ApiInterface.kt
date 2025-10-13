package com.dhananjayanidhi.apiUtils

import com.dhananjayanidhi.models.CommonModel
import com.dhananjayanidhi.models.accountopen.AccountOpenModel
import com.dhananjayanidhi.models.addressentry.AddressEntryModel
import com.dhananjayanidhi.models.customeradd.CustomerAddModel
import com.dhananjayanidhi.models.customerdetail.CustomerDetailModel
import com.dhananjayanidhi.models.customerlist.CustomerListModel
import com.dhananjayanidhi.models.customerlistv1.CustomerListV1Model
import com.dhananjayanidhi.models.customersearch.CustomerSearchModel
import com.dhananjayanidhi.models.dashboard.DashboardModel
import com.dhananjayanidhi.models.depositscheme.DepositSchemeModel
import com.dhananjayanidhi.models.kycentry.KycEntryModel
import com.dhananjayanidhi.models.loanamount.LoanAmountModel
import com.dhananjayanidhi.models.loandetails.LoanDetailsModel
import com.dhananjayanidhi.models.loanlist.LoanListModel
import com.dhananjayanidhi.models.loansearch.LoanSearchModel
import com.dhananjayanidhi.models.loansearch1.LoanSearch1Model
import com.dhananjayanidhi.models.paymentcollection.PaymentCollectionModel
import com.dhananjayanidhi.models.search.SearchModel
import com.dhananjayanidhi.models.transaction.TransactionModel
import com.dhananjayanidhi.models.usermodel.UserLoginModel
import com.dhananjayanidhi.parameters.AccountOpenParams
import com.dhananjayanidhi.parameters.AddressEntryParams
import com.dhananjayanidhi.parameters.CustomerAddParams
import com.dhananjayanidhi.parameters.CustomerDetailsParams
import com.dhananjayanidhi.parameters.CustomerSearchParams
import com.dhananjayanidhi.parameters.OtpParams
import com.dhananjayanidhi.parameters.PaymentCollectionParams
import com.dhananjayanidhi.parameters.SearchParams
import com.dhananjayanidhi.parameters.SignupParams
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiInterface {
    @POST(ApiUrlEndpoint.SIGNUP_API)
    fun signupUserApi(@Body signupParams: SignupParams?): Call<CommonModel?>?

    @POST(ApiUrlEndpoint.VERIFY_OTP_API)
    fun otpApi(@Body otpParams: OtpParams?): Call<UserLoginModel?>?

    @POST(ApiUrlEndpoint.LOGOUT_API)
    fun logoutApi(): Call<CommonModel?>?

    @GET(ApiUrlEndpoint.CUSTOMER_LIST_API)
    fun customerListApi(): Call<CustomerListModel?>?

    @GET(ApiUrlEndpoint.CUSTOMER_LIST_API_V1)
    fun customerListV1Api(): Call<CustomerListV1Model?>?

    @GET
    fun getCustomerList(@Url url: String): Call<CustomerListV1Model>

    @GET(ApiUrlEndpoint.LOAN_LIST_API)
    fun loanListApi(): Call<LoanListModel?>?

    @GET
    fun loanListNextPageApi(@Url url: String): Call<LoanListModel?>?

    @POST(ApiUrlEndpoint.LOAN_DETAILS_API)
    fun loanListDetailsApi(@Body customerDetailsParams: CustomerDetailsParams): Call<LoanDetailsModel?>?

    @POST(ApiUrlEndpoint.CUSTOMER_DETAILS_API)
    fun customerDetailsApi(@Body customerDetailsParams: CustomerDetailsParams): Call<CustomerDetailModel?>?

    @GET(ApiUrlEndpoint.CUSTOMER_DETAILS_V1_API)
    fun customerDetailsV1Api(@Query("customer_id") customerId:String,
                             @Query("account_id") accountId:String): Call<CustomerDetailModel?>?

    @POST(ApiUrlEndpoint.PAYMENT_COLLECTION_API)
    fun addCustomerAmountApi(@Body paymentCollectionParams: PaymentCollectionParams): Call<PaymentCollectionModel?>?

    @POST(ApiUrlEndpoint.LOAN_AMOUNT_COLLECTION_API)
    fun loanAmountAddApi(@Body paymentCollectionParams: PaymentCollectionParams): Call<LoanAmountModel?>?

    @GET(ApiUrlEndpoint.DASHBOARD_API)
    fun dashboardApi():Call<DashboardModel?>?

    @GET(ApiUrlEndpoint.TRANSACTIONS_API)
    fun transactionApi(@Query("account_number") accountNumber:String,
                       @Query("date") date:String):Call<TransactionModel?>?

    @POST(ApiUrlEndpoint.SEARCH_CUSTOMER_API)
    fun searchCustomerApi(@Body customerSearchParams: CustomerSearchParams):Call<SearchModel?>?

    @POST(ApiUrlEndpoint.SEARCH_CUSTOMER_V1_API)
    fun searchCustomerV1Api(@Body customerSearchParams: CustomerSearchParams):Call<JsonObject>

    @POST(ApiUrlEndpoint.CUSTOMER_SEARCH_API)
    fun searchCustomerNameApi(@Body searchParams: SearchParams):Call<CustomerSearchModel>

    @POST(ApiUrlEndpoint.LOAN_SEARCH_API)
    fun searchLoanNameApi(@Body searchParams: SearchParams):Call<LoanSearch1Model>

    @POST(ApiUrlEndpoint.MEMBER_PERSONAL_INFO_API)
    fun addCustomerApi(@Body customerAddParams: CustomerAddParams):Call<CustomerAddModel?>?

    @GET(ApiUrlEndpoint.DEPOSIT_SCHEME_API)
    fun depositSchemeApi():Call<DepositSchemeModel?>?

    @POST(ApiUrlEndpoint.ADDRESS_INFO_API)
    fun addressInfoApi(@Body addressEntryParams: AddressEntryParams):Call<AddressEntryModel?>?

    @Multipart
    @POST(ApiUrlEndpoint.DOCUMENT_UPDATE_API)
    fun kycEntryApi(
        @Part aadharCardImage: MultipartBody.Part?,
        @Part aadharCardImage2: MultipartBody.Part?,
        @Part panCardImage: MultipartBody.Part?,
        @Part customerImage: MultipartBody.Part?,
        @Part signatureImage: MultipartBody.Part?,
        @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>
    ):Call<KycEntryModel?>?

    @POST(ApiUrlEndpoint.OPEN_ACCOUNT_API)
    fun openAccountApi(@Body accountOpenParams: AccountOpenParams):Call<AccountOpenModel?>?
}