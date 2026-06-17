package com.undef.superahorro.BossioCorrea.data.remote

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface VtexApiService {

    @Headers(
        "Accept: application/json",
        "User-Agent: Mozilla/5.0 (Android) SuperAhorroApp"
    )
    @GET("api/catalog_system/pub/products/search")
    suspend fun buscarProductos(
        @Query("_from") from: Int,
        @Query("_to") to: Int
    ): List<VtexProducto>
}
