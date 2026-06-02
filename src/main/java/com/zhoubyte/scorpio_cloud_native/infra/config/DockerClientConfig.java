package com.zhoubyte.scorpio_cloud_native.infra.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.zhoubyte.scorpio_cloud_native.infra.config.common.DockerComponentConfig;
import com.zhoubyte.scorpio_cloud_native.infra.config.common.ScorpioInfraConfig;
import com.zhoubyte.scorpio_cloud_native.infra.exception.InfrastructureException;
import io.micrometer.common.util.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.net.URI;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.time.Duration;

@Configuration
public class DockerClientConfig {

    @Bean("dockerClient")
    public DockerClient initDockerClient(ScorpioInfraConfig scorpioInfraConfig) {
        DockerComponentConfig docker = scorpioInfraConfig.getConfig().getDocker();
        validateDockerConfig(docker);
        
        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(docker.getHost())
                .withDockerTlsVerify(docker.getTlsVerify())
                .withDockerCertPath(docker.getCertPath());

        DockerComponentConfig.DockerAuthConfig auth = docker.getAuth();
        if(auth != null) {
            builder.withRegistryUsername(auth.getUsername())
                    .withRegistryPassword(auth.getPassword())
                    .withRegistryEmail(auth.getEmail())
                    .withRegistryUrl(auth.getUrl());
        }
        DefaultDockerClientConfig defaultDockerClientConfig = builder.build();

        DockerComponentConfig.DockerHttpConfig http = docker.getHttp();
        ApacheDockerHttpClient.Builder httpClientBuilder = new ApacheDockerHttpClient.Builder()
                .dockerHost(URI.create(docker.getHost()))
                .connectionTimeout(Duration.ofMillis(http.getConnectionTimeout()))
                .responseTimeout(Duration.ofMillis(http.getResponseTimeout()))
                .maxConnections(http.getMaxConnect());

        DockerComponentConfig.DockerSslConfig sslConfig = docker.getSsl();
        if (sslConfig != null && Boolean.TRUE.equals(sslConfig.getEnabled())) {
            SSLContext sslContext = buildSSLContext(sslConfig);
            httpClientBuilder.sslConfig(() -> sslContext);
        } else {
            httpClientBuilder.sslConfig(defaultDockerClientConfig.getSSLConfig());
        }

        ApacheDockerHttpClient dockerHttpClient = httpClientBuilder.build();

        return DockerClientImpl.getInstance(defaultDockerClientConfig, dockerHttpClient);
    }

    private SSLContext buildSSLContext(DockerComponentConfig.DockerSslConfig sslConfig) {
        try {
            SSLContext sslContext = SSLContext.getInstance(sslConfig.getProtocol());
            
            KeyManagerFactory kmf = null;
            TrustManagerFactory tmf = null;
            
            DockerComponentConfig.DockerSslConfig.KeyStoreConfig keyStoreConfig = sslConfig.getKeyStore();
            if (keyStoreConfig != null && StringUtils.isNotEmpty(keyStoreConfig.getPath())) {
                KeyStore keyStore = KeyStore.getInstance(keyStoreConfig.getType());
                try (FileInputStream fis = new FileInputStream(keyStoreConfig.getPath())) {
                    keyStore.load(fis, keyStoreConfig.getPassword() != null ? 
                            keyStoreConfig.getPassword().toCharArray() : null);
                }
                kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                kmf.init(keyStore, keyStoreConfig.getPassword() != null ? 
                        keyStoreConfig.getPassword().toCharArray() : null);
            }
            
            DockerComponentConfig.DockerSslConfig.TrustStoreConfig trustStoreConfig = sslConfig.getTrustStore();
            if (trustStoreConfig != null && StringUtils.isNotEmpty(trustStoreConfig.getPath())) {
                KeyStore trustStore = KeyStore.getInstance(trustStoreConfig.getType());
                try (FileInputStream fis = new FileInputStream(trustStoreConfig.getPath())) {
                    trustStore.load(fis, trustStoreConfig.getPassword() != null ? 
                            trustStoreConfig.getPassword().toCharArray() : null);
                }
                tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(trustStore);
            }
            
            sslContext.init(
                    kmf != null ? kmf.getKeyManagers() : null,
                    tmf != null ? tmf.getTrustManagers() : null,
                    new SecureRandom()
            );
            
            return sslContext;
        } catch (Exception e) {
            throw new InfrastructureException("Failed to build SSL configuration: " + e.getMessage(), e);
        }
    }

    /**
     * 验证配置参数
     * @param docker 配置参数
     */
    private void validateDockerConfig(DockerComponentConfig docker) {
        if (docker == null) {
            throw new InfrastructureException("Docker configuration is null");
        }
        
        if (StringUtils.isEmpty(docker.getHost())) {
            throw new InfrastructureException("Docker host is empty");
        }
        
        DockerComponentConfig.DockerHttpConfig http = docker.getHttp();
        if (http != null) {
            if (http.getConnectionTimeout() == null || http.getConnectionTimeout() <= 0) {
                throw new InfrastructureException("Docker HTTP connection timeout must be greater than 0");
            }
            if (http.getResponseTimeout() == null || http.getResponseTimeout() <= 0) {
                throw new InfrastructureException("Docker HTTP response timeout must be greater than 0");
            }
            if (http.getMaxConnect() <= 0) {
                throw new InfrastructureException("Docker HTTP max connections must be greater than 0");
            }
        }
        
        if (Boolean.TRUE.equals(docker.getTlsVerify()) && StringUtils.isEmpty(docker.getCertPath())) {
            throw new InfrastructureException("Docker cert path is required when TLS is enabled");
        }
        
        DockerComponentConfig.DockerSslConfig ssl = docker.getSsl();
        if (ssl != null && Boolean.TRUE.equals(ssl.getEnabled())) {
            validateSSLConfig(ssl);
        }
    }
    
    private void validateSSLConfig(DockerComponentConfig.DockerSslConfig ssl) {
        if (StringUtils.isEmpty(ssl.getProtocol())) {
            throw new InfrastructureException("SSL protocol is required when SSL is enabled");
        }
        
        DockerComponentConfig.DockerSslConfig.KeyStoreConfig keyStore = ssl.getKeyStore();
        if (keyStore != null) {
            if (StringUtils.isEmpty(keyStore.getPath())) {
                throw new InfrastructureException("KeyStore path is required when KeyStore is configured");
            }
            if (StringUtils.isEmpty(keyStore.getPassword())) {
                throw new InfrastructureException("KeyStore password is required when KeyStore is configured");
            }
        }
        
        DockerComponentConfig.DockerSslConfig.TrustStoreConfig trustStore = ssl.getTrustStore();
        if (trustStore != null) {
            if (StringUtils.isEmpty(trustStore.getPath())) {
                throw new InfrastructureException("TrustStore path is required when TrustStore is configured");
            }
            if (StringUtils.isEmpty(trustStore.getPassword())) {
                throw new InfrastructureException("TrustStore password is required when TrustStore is configured");
            }
        }
        
        if (keyStore == null && trustStore == null) {
            throw new InfrastructureException("At least KeyStore or TrustStore must be configured when SSL is enabled");
        }
    }
}
