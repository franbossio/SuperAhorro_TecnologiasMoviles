package com.undef.superahorro.BossioCorrea.data.remote

import com.google.gson.annotations.SerializedName

data class VtexProducto(
    @SerializedName("productName") val productName: String,
    @SerializedName("items") val items: List<VtexItem>?
)

data class VtexItem(
    @SerializedName("sellers") val sellers: List<VtexSeller>?
)

data class VtexSeller(
    @SerializedName("commertialOffer") val commertialOffer: VtexOffer?
)

data class VtexOffer(
    @SerializedName("Price")     val price: Double,
    @SerializedName("ListPrice") val listPrice: Double
)
