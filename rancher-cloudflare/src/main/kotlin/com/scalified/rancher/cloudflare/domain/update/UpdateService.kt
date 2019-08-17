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

package com.scalified.rancher.cloudflare.domain.update

import com.scalified.rancher.cloudflare.domain.cloudflare.dns.DnsService
import com.scalified.rancher.cloudflare.domain.rancher.ingress.IngressService
import mu.KotlinLogging

/**
 * @author shell
 * @since 2019-07-31
 */
private val logger = KotlinLogging.logger {}

class UpdateService(private val ingressService: IngressService, private val dnsService: DnsService) {

	fun update() {
		val hosts = ingressService.hosts()
		if (hosts.isNotEmpty()) {
			val records = dnsService.records()
			val added = dnsService.findAdded(hosts, records)
			val removed = dnsService.findRemoved(hosts, records)

			if (added.isEmpty() && removed.isEmpty()) {
				logger.info { "Skipping update since nothing changed" }
			} else {
				if (added.isNotEmpty()) added.forEach(dnsService::add)
				if (removed.isNotEmpty()) removed.forEach(dnsService::remove)
			}
		} else {
			logger.info { "Skipping update since no ingress entries found" }
		}
	}

}
