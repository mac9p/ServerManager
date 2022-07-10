package com.mac9p.servermanager.services;

import com.mac9p.servermanager.model.Server;
import com.mac9p.servermanager.repositories.ServerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.NoResultException;
import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
@Slf4j
public class ServerService {
    private final ServerRepository serverRepository;

    public ServerService(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    Optional<Server> findByIp(String ip) {
        log.info("finding server with ip: {}", ip);
        return serverRepository.findByIpAddress(ip);
    }

    Optional<Server> createServer(Server server) {
        log.info("adding server to database: {}", server.getName());
        setServerImage(server);
        return Optional.of(serverRepository.save(server));
    }

    private void setServerImage(Server server) {
        String[] imageUrls = {"server1", "server2", "server3"};
        server.setImageUrl(ServletUriComponentsBuilder.
                fromCurrentContextPath().
                path("/server/image/") + imageUrls[new Random().nextInt(3)]);
    }

    List<Server> findAllServers() {
        log.info("returning all servers");
        return serverRepository.findAll();
    }

    Optional<Server> updateServer(Server server) {
        log.info("updating server with ip: {}", server.getIpAddress());
        return Optional.of(serverRepository.save(server));
    }

    Server checkStatus(String ipAddress) throws IOException {
        log.info("Checking status of server with ip: {}", ipAddress);
        Server server = serverRepository.findByIpAddress(ipAddress)
                .orElseThrow(NoResultException::new);
        InetAddress address = InetAddress.getByName(ipAddress);
        server.setIsActive(address.isReachable(10000));
        return serverRepository.save(server);
    }

    public void deleteServer(Long id) {
        log.info("Deleting server with id: {}", id);
        serverRepository.deleteById(id);
    }


}
