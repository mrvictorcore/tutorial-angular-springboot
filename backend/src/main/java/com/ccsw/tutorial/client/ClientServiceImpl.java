package com.ccsw.tutorial.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientRepository clientRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Client> findAll() {

        return (List<Client>) this.clientRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Client get(Long id) {
        return this.clientRepository.findById(id).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Long id, ClientDto dto) throws Exception {

        if (existsByName(dto.getName())) {
            throw new Exception("El nombre del cliente ya existe");
        } else {
            Client existingClient = clientRepository.findByName(dto.getName());
            if (existingClient != null && !existingClient.getId().equals(id)) {
                throw new Exception("El nombre del cliente ya existe");
            }
        }

        Client client;

        if (id == null) {
            client = new Client();
        } else {
            client = this.get(id);
        }

        client.setName(dto.getName());

        this.clientRepository.save(client);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) throws Exception {
        Client client = this.get(id);
        if (client == null)
            throw new Exception("Client not found");

        this.clientRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return this.clientRepository.findByName(name) != null;
    }
}
