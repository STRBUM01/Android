/*
 * Copyright (c) 2018 DuckDuckGo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duckduckgo.app.statistics

import com.duckduckgo.app.InstantSchedulersRule
import com.duckduckgo.app.global.device.DeviceInfo
import com.duckduckgo.app.statistics.api.PixelService
import com.duckduckgo.app.statistics.model.Atb
import com.duckduckgo.app.statistics.pixels.ApiBasedPixel
import com.duckduckgo.app.statistics.pixels.Pixel.PixelName.*
import com.duckduckgo.app.statistics.store.StatisticsDataStore
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Completable
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock

class ApiBasedPixelTest {

    @get:Rule
    @Suppress("unused")
    val schedulers = InstantSchedulersRule()

    @Mock
    val mockPixelService: PixelService = mock()

    @Mock
    val mockStatisticsDataStore: StatisticsDataStore = mock()

    @Mock
    val mockVariantManager: VariantManager = mock()

    @Mock
    val mockDeviceInfo: DeviceInfo = mock()

    @Test
    fun whenPixelFiredThenPixelServiceCalledWithCorrectAtbAndVariant() {
        whenever(mockPixelService.fire(any(), any(), any(), anyOrNull())).thenReturn(Completable.complete())
        whenever(mockStatisticsDataStore.atb).thenReturn(Atb("atb"))
        whenever(mockVariantManager.getVariant()).thenReturn(Variant("variant"))
        whenever(mockDeviceInfo.formFactor()).thenReturn(DeviceInfo.FormFactor.PHONE)

        val pixel = ApiBasedPixel(mockPixelService, mockStatisticsDataStore, mockVariantManager, mockDeviceInfo)
        pixel.fire(PRIVACY_DASHBOARD_OPENED)

        verify(mockPixelService).fire("mp", "phone", "atbvariant", emptyMap())
    }

    @Test
    fun whenPixelFiredThenPixelServiceCalledWithCorrectAtb() {
        whenever(mockPixelService.fire(any(), any(), any(), any())).thenReturn(Completable.complete())
        whenever(mockStatisticsDataStore.atb).thenReturn(Atb("atb"))
        whenever(mockVariantManager.getVariant()).thenReturn(VariantManager.DEFAULT_VARIANT)
        whenever(mockDeviceInfo.formFactor()).thenReturn(DeviceInfo.FormFactor.PHONE)

        val pixel = ApiBasedPixel(mockPixelService, mockStatisticsDataStore, mockVariantManager, mockDeviceInfo)
        pixel.fire(FORGET_ALL_EXECUTED)

        verify(mockPixelService).fire("mf", "phone", "atb", emptyMap())
    }

    @Test
    fun whenPixelFiredTabletFormFactorThenPixelServiceCalledWithTabletParameter() {
        whenever(mockPixelService.fire(any(), any(), any(), any())).thenReturn(Completable.complete())
        whenever(mockDeviceInfo.formFactor()).thenReturn(DeviceInfo.FormFactor.TABLET)

        val pixel = ApiBasedPixel(mockPixelService, mockStatisticsDataStore, mockVariantManager, mockDeviceInfo)
        pixel.fire(APP_LAUNCH)

        verify(mockPixelService).fire("ml", "tablet", "", emptyMap())
    }

    @Test
    fun whenPixelFiredWithNoAtbThenPixelServiceCalledWithCorrectPixelNameAndNoAtb() {
        whenever(mockPixelService.fire(any(), any(), any(), any())).thenReturn(Completable.complete())
        whenever(mockDeviceInfo.formFactor()).thenReturn(DeviceInfo.FormFactor.PHONE)

        val pixel = ApiBasedPixel(mockPixelService, mockStatisticsDataStore, mockVariantManager, mockDeviceInfo)
        pixel.fire(APP_LAUNCH)

        verify(mockPixelService).fire("ml", "phone", "", emptyMap())
    }

    @Test
    fun whenPixelFiredWithAdditionalParametersThenPixelServiceCalledWithAdditionalParameters() {
        whenever(mockPixelService.fire(any(), any(), any(), any())).thenReturn(Completable.complete())
        whenever(mockStatisticsDataStore.atb).thenReturn(Atb("atb"))
        whenever(mockVariantManager.getVariant()).thenReturn(Variant("variant"))
        whenever(mockDeviceInfo.formFactor()).thenReturn(DeviceInfo.FormFactor.PHONE)

        val pixel = ApiBasedPixel(mockPixelService, mockStatisticsDataStore, mockVariantManager, mockDeviceInfo)
        val params = mapOf("param1" to "value1", "param2" to "value2")
        pixel.fire(PRIVACY_DASHBOARD_OPENED, params)
        verify(mockPixelService).fire("mp", "phone", "atbvariant", params)
    }

    @Test
    fun whenPixelFiredWithoutAdditionalParametersThenPixelServiceCalledWithNoParameters() {
        whenever(mockPixelService.fire(any(), any(), any(), any())).thenReturn(Completable.complete())
        whenever(mockStatisticsDataStore.atb).thenReturn(Atb("atb"))
        whenever(mockVariantManager.getVariant()).thenReturn(Variant("variant"))
        whenever(mockDeviceInfo.formFactor()).thenReturn(DeviceInfo.FormFactor.PHONE)

        val pixel = ApiBasedPixel(mockPixelService, mockStatisticsDataStore, mockVariantManager, mockDeviceInfo)
        val params = emptyMap<String, String>()
        pixel.fire(PRIVACY_DASHBOARD_OPENED)
        verify(mockPixelService).fire("mp", "phone", "atbvariant", params)
    }
}