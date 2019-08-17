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

package com.scalified.rancher.cloudflare.infrastructure

import com.scalified.rancher.cloudflare.domain.cloudflare.CloudflareClient
import com.scalified.rancher.cloudflare.domain.cloudflare.dns.DnsService
import com.scalified.rancher.cloudflare.domain.rancher.RancherClient
import com.scalified.rancher.cloudflare.domain.rancher.ingress.IngressService
import com.scalified.rancher.cloudflare.domain.update.UpdateScheduler
import com.scalified.rancher.cloudflare.domain.update.UpdateService
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.context.support.beans

/**
 * @author shell
 * @since 2019-07-29
 */
fun beans() = beans {

	bean<RancherClient>()
	bean<IngressService>()

	bean<CloudflareClient>()

	bean<DnsService>()
	bean<UpdateService>()
	bean<UpdateScheduler>()

	bean {
		PropertySourcesPlaceholderConfigurer().apply {
			setPlaceholderPrefix("%{")
			setIgnoreUnresolvablePlaceholders(true)
		}
	}

}
