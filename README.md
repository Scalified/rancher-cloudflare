# Rancher Cloudflare #

[![Build Status](https://github.com/Scalified/rancher-cloudflare/actions/workflows/docker-image.yml/badge.svg)](https://github.com/Scalified/rancher-cloudflare/actions)
[![Docker Pulls](https://img.shields.io/docker/pulls/scalified/rancher-cloudflare.svg)](https://hub.docker.com/r/scalified/rancher-cloudflare)

## Description

The project automatically synchronizes [Rancher](https://rancher.com/) ingress records to [Cloudflare](https://www.cloudflare.com/) DNS 

## Dockerhub

**`docker pull scalified/rancher-cloudflare:<version>`**

## Version

| Version              | Rancher API Version | Cloudflare API Version |
|----------------------|---------------------|------------------------|
| **1.1.0** **latest** | v3                  | v4                     |
| **1.0.0**            | v3                  | v4                     |

### How-To

#### Usage

The following environment variables must be provided:

| Environment Variable  | Example                                                | Description                                                                                            |
|-----------------------|--------------------------------------------------------|--------------------------------------------------------------------------------------------------------|
| RANCHER_URL           | https://rancher.example.com                            | Rancher Server URL                                                                                     |
| RANCHER_ACCESS_KEY    | token-dukhk                                            | The token’s username. See [API Keys](https://rancher.com/docs/rancher/v2.x/en/user-settings/api-keys/) |
| RANCHER_SECRET_KEY    | f6sdd2gt2t5gs779fpg9km6fl4d46nkfcnknqzjwszwb6jm4qfh48y | The token’s password. See [API Keys](https://rancher.com/docs/rancher/v2.x/en/user-settings/api-keys/) |
| CLOUDFLARE_IP_ADDRESS | 50.20.120.200                                          | IP Address to be written as A record content                                                           |
| CLOUDFLARE_PROXIED    | false                                                  | Whether proxy needs to be enabled on Cloudflare                                                        |
| CLOUDFLARE_ZONE_ID    | 4ff62716278c165a1592992bde7ff64d                       | Cloudflare Zone Id. See [Api Documentation](https://api.cloudflare.com/#getting-started-resource-ids)  |
| CLOUDFLARE_API_KEY    | 794ce33565fec7fc069e0cdfb9c43a0dd1221                  | Cloudflare API Key See [Api Documentation](https://api.cloudflare.com/#getting-started-resource-ids)   |
| CLOUDFLARE_EMAIL      | mail@example.com                                       | Cloudflare account email                                                                               |

#### Assembling

1. Build the Project:  
   `./gradlew clean build`
2. Build Docker image:  
   `docker build . -t scalified/rancher-cloudflare`

## Scalified Links

* [Scalified](https://www.scalified.com)
* [Scalified Official Facebook Page](https://www.facebook.com/scalified)
* <a href="mailto:info@scalified.com?subject=[Rancher Cloudflare]: Proposals And Suggestions">Scalified Support</a>
