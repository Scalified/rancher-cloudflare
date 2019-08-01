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

package com.scalified.rancher.cloudflare.domain.dns

import com.scalified.rancher.cloudflare.domain.cloudflare.CloudflareClient
import com.scalified.rancher.cloudflare.domain.cloudflare.CloudflareDnsCache
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value

/**
 * @author shell
 * @since 2019-07-31
 */
private val logger = KotlinLogging.logger {}

class DnsService(
		private val client: CloudflareClient,
		@Value("%{spring.application.name}") private val applicationName: String,
		@Value("%{IP_ADDRESS}") private val ipAddress: String
) {

	fun records(): List<DnsRecord> = client.records().filter { it.type in listOf(A, TXT) }

	fun add(host: String) {
		client.add(DnsRecord(A, host, ipAddress, true))
		client.add(DnsRecord(TXT, host, txtContent(host)))
		logger.info { "Added Cloudflare DNS record for $host host" }
	}

	fun remove(record: DnsRecord) {
		record.id?.let {
			client.remove(it)
			logger.info { "Removed Cloudflare $record DNS record" }
		}
	}

	fun findAdded(hosts: List<String>): List<String> = hosts.filterNot { host ->
		CloudflareDnsCache.records().any { it.name.startsWith(host) }
	}

	fun findRemoved(hosts: List<String>): List<DnsRecord> {
		val grouped = CloudflareDnsCache.records().groupBy { it.name }
				.filterValues { records ->
					records.size > 1
							&& records.any { it.type == A && it.content == ipAddress }
							&& records.any { record ->
						record.type == TXT && hosts.none { record.content == txtContent(it) }
					}
				}

		return grouped.values.flatten()
	}

	private fun txtContent(host: String) = "app=$applicationName;host=$host;ipAddress=$ipAddress"

}
