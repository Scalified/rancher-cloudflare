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

package com.scalified.rancher.cloudflare.domain.cloudflare.dns

import com.scalified.rancher.cloudflare.domain.cloudflare.CloudflareClient
import com.scalified.rancher.cloudflare.infrastructure.AppProperties
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value

/**
 * @author shell
 * @since 2019-07-31
 */
private val logger = KotlinLogging.logger {}

class DnsService(private val client: CloudflareClient, private val properties: AppProperties) {

	fun records(): List<DnsRecord> = client.records().filter { it.type in listOf(A, TXT) }

	fun findAdded(hosts: List<String>, records: List<DnsRecord>): List<DnsRecord> = hosts.filterNot { host ->
		records.any { it.type == A && it.name.startsWith(host) }
	}.flatMap { host ->
		listOf(
			DnsRecord(A, host, properties.cloudflare.ipAddress, properties.cloudflare.proxied),
			DnsRecord(TXT, host, txtContent(host))
		).filterNot {
			it.type == TXT && records.any { record -> record.type == TXT && record.content == it.content }
		}
	}

	fun findRemoved(hosts: List<String>, records: List<DnsRecord>): List<DnsRecord> = records.groupBy { it.name }
		.filterValues { recs ->
			recs.size > 1
					&& recs.any { it.type == A && it.content == properties.cloudflare.ipAddress }
					&& recs.any { record ->
				record.type == TXT && hosts.none { record.content == txtContent(it) }
			}
		}.values.flatten()

	fun add(record: DnsRecord) {
		val added = client.add(record)
		logger.info { "Added $added Cloudflare DNS record" }
	}

	fun remove(record: DnsRecord) {
		record.id?.let {
			client.remove(it)
			logger.info { "Removed Cloudflare $record DNS record" }
		}
	}

	private fun txtContent(host: String) =
		"app=${properties.name};host=$host;ipAddress=${properties.cloudflare.ipAddress}"

}
