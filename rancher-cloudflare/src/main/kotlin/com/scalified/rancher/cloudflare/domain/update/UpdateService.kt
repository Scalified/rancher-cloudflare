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

import com.scalified.rancher.cloudflare.domain.cloudflare.CloudflareDnsCache
import com.scalified.rancher.cloudflare.domain.dns.DnsService
import com.scalified.rancher.cloudflare.domain.rancher.ingress.IngressService
import mu.KotlinLogging

/**
 * @author shell
 * @since 2019-07-31
 */
private val logger = KotlinLogging.logger {}

class UpdateService(private val ingressService: IngressService, private val dnsService: DnsService) {

	fun update() {
		if (CloudflareDnsCache.isUpdating.get()) {
			logger.debug { "Skipping ingress update since DNS cache is currently updating" }
		} else {
			logger.debug { "Updating ingress DNS records" }
			CloudflareDnsCache.isUpdating.set(true)
			try {
				val hosts = ingressService.hosts()
				if (hosts.isNotEmpty()) {
					if (CloudflareDnsCache.isEmpty()) {
						logger.info { "Populating DNS cache with Cloudflare existing DNS entries" }
						CloudflareDnsCache.populate(dnsService.records())
						logger.info {
							"Populated DNS cache with " +
									"${CloudflareDnsCache.size()} existing Cloudflare DNS record(s)"
						}
					}
					val added = dnsService.findAdded(hosts)
					val removed = dnsService.findRemoved(hosts)
					if (added.isEmpty() && removed.isEmpty()) {
						logger.debug { "Skipping update since nothing changed" }
					} else {
						if (added.isNotEmpty()) added.forEach(dnsService::add)
						if (removed.isNotEmpty()) removed.forEach(dnsService::remove)
						CloudflareDnsCache.populate(dnsService.records())
					}
				} else {
					logger.trace { "Skipping update since no ingress entries found" }
				}
			} finally {
				CloudflareDnsCache.isUpdating.set(false)
			}
		}
	}

}
