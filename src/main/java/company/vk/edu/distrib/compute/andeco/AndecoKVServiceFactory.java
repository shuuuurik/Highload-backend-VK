package company.vk.edu.distrib.compute.andeco;

import company.vk.edu.distrib.compute.KVService;
import company.vk.edu.distrib.compute.KVServiceFactory;

import java.io.IOException;

public class AndecoKVServiceFactory extends KVServiceFactory {
    @Override
    protected KVService doCreate(int port) throws IOException {
        return new KVServiceImpl(port, new FileDao(ServerConfigConstants.BASE_DIR));
    }
}
