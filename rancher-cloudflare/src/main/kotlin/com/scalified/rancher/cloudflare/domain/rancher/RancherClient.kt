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

package com.scalified.rancher.cloudflare.domain.rancher

import com.scalified.rancher.cloudflare.domain.rancher.ingress.Ingress
import com.scalified.rancher.cloudflare.domain.rancher.ingress.IngressesDto
import com.scalified.rancher.cloudflare.domain.rancher.project.Project
import com.scalified.rancher.cloudflare.domain.rancher.project.ProjectsDto
import com.scalified.rancher.cloudflare.infrastructure.commons.HealthResponseErrorHandler
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpHeaders
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.getForObject
import java.util.concurrent.atomic.AtomicReference

/**
 * @author shell
 * @since 2019-07-30
 */
private val logger = KotlinLogging.logger {}

class RancherClient(
		@Value("%{RANCHER_URL}") private val url: String,
		@Value("%{RANCHER_ACCESS_KEY}") private val accessKey: String,
		@Value("%{RANCHER_SECRET_KEY}") private val secretKey: String,
		@Value("%{spring.application.name}") private val userAgent: String
) : HealthIndicator {

	private val client = RestTemplateBuilder().basicAuthentication(accessKey, secretKey)
			.interceptors(listOf(ClientHttpRequestInterceptor { request, body, execution ->
				request.headers[HttpHeaders.USER_AGENT] = listOf(userAgent)
				execution.execute(request, body)
			}))
			.rootUri("$url/v3")
			.errorHandler(HealthResponseErrorHandler(health))
			.build()

	fun projects(): List<Project> {
		fun projects(url: String): List<Project> {
			val result = client.getForObject<ProjectsDto>(url)
			val next = result?.next()
			val projects = result?.projects.orEmpty()
			return if (next == null) projects else projects + projects(next)
		}

		val projects = projects("/projects?limit=100")
		health.set(Health.up().build())
		logger.debug { "Fetched ${projects.size} project(s) from Rancher" }
		return projects
	}

	fun ingresses(projectId: String): List<Ingress> {
		fun ingresses(url: String): List<Ingress> {
			val result = client.getForObject<IngressesDto>(url)
			val next = result?.next()
			val ingresses = result?.ingresses.orEmpty()
			return if (next == null) ingresses else ingresses + ingresses(next)
		}

		val ingresses = ingresses(url = "/projects/$projectId/ingresses?limit=100")
		health.set(Health.up().build())
		logger.debug { "Fetched ${ingresses.size} ingress(es) for $projectId project from Rancher" }
		return ingresses
	}

	override fun health(): Health = health.get()

	companion object {

		@JvmStatic
		private val health = AtomicReference(Health.up().build())

	}

}
