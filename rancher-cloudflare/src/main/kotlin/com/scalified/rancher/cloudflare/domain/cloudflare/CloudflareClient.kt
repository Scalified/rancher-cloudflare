/*
 * MIT License
 *
 * Copyright (c) 2019 Scalified
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.scalified.rancher.cloudflare.domain.cloudflare

import com.scalified.rancher.cloudflare.domain.cloudflare.dns.DnsRecord
import com.scalified.rancher.cloudflare.infrastructure.commons.HealthResponseErrorHandler
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.getForObject
import org.springframework.web.client.postForObject
import java.util.concurrent.atomic.AtomicReference

/**
 * @author shell
 * @since 2019-07-30
 */
private val logger = KotlinLogging.logger {}

class CloudflareClient(
		@Value("%{CLOUDFLARE_EMAIL}") private val email: String,
		@Value("%{CLOUDFLARE_ZONE_ID}") private val zoneId: String,
		@Value("%{CLOUDFLARE_API_KEY}") private val apiKey: String
) : HealthIndicator {

	private val client = RestTemplateBuilder().interceptors(
			listOf(ClientHttpRequestInterceptor { request, body, execution ->
				request.headers["X-Auth-Email"] = listOf(email)
				request.headers["X-Auth-Key"] = listOf(apiKey)
				execution.execute(request, body)
			})
	).rootUri("https://api.cloudflare.com/client/v4/zones/$zoneId")
			.errorHandler(HealthResponseErrorHandler(health))
			.build()

	fun records(): List<DnsRecord> {
		fun records(page: Int): List<DnsRecord> {
			val result = client.getForObject<DnsRecordsGetDto>("/dns_records?page=$page&per_page=100")
			val pageCount = result?.resultInfo?.pageCount ?: page
			val records = result?.records.orEmpty()
			return if (pageCount > page) records + records(page + 1) else records
		}

		val records = records(1)
		health.set(Health.up().build())
		logger.debug { "Fetched ${records.size} DNS records from Cloudflare" }
		return records
	}

	fun add(record: DnsRecord): DnsRecord {
		val result = client.postForObject<DnsRecordPostResponseDto>("/dns_records", HttpEntity(record))
		health.set(Health.up().build())
		logger.debug { "Added Cloudflare $record DNS record" }
		return record.copy(id = result?.dnsRecord?.id)
	}

	fun remove(id: String) {
		client.delete("/dns_records/$id")
		health.set(Health.up().build())
		logger.debug { "Deleted Cloudflare $id DNS record" }
	}

	override fun health(): Health = health.get()

	companion object {

		@JvmStatic
		private val health = AtomicReference(Health.up().build())

	}

}
