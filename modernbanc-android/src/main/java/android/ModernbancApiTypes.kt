package android

import com.google.gson.annotations.SerializedName

data class MdbApiError(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String
)

data class CreateSecretResponse(
    @SerializedName("result") val result: List<Secret>
)

data class Secret(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("workspace_id") val workspace_id: String,
    @SerializedName("original_value_type") val original_value_type: String?
)
