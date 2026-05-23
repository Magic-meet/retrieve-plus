package edu.njucm.retrievejava.service.Impl;

import edu.njucm.retrievejava.service.FileService;
import edu.njucm.retrievejava.untils.FileTools;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileServiceImp implements FileService {

    @Value("${fastdfs.tracker-servers:fastdfs:22122}")
    private String trackerServers;

    private final Object fastdfsInitLock = new Object();
    private volatile String initializedTrackerServers;
    /***
     * 上传文件到FastDFS
     * @param multipartFile
     * @return
     */
    public String[] uploadFileToDFS(MultipartFile multipartFile) throws MyException, IOException {
        StorageClient client = createStorageClient();
        byte[] fileBytes = multipartFile.getBytes();
        String originalFilename = multipartFile.getOriginalFilename();
        return client.upload_file(fileBytes, FileTools.getFileExtName(originalFilename), null);
    }

    public byte[] downloadFileFromDFS(String storageGroup, String storagePath) throws MyException, IOException {
        StorageClient client = createStorageClient();
        return client.download_file(storageGroup, storagePath);
    }

    public int deleteFileFromDFS(String storageGroup, String storagePath) throws MyException, IOException {
        StorageClient client = createStorageClient();
        return client.delete_file(storageGroup, storagePath);
    }

    private StorageClient createStorageClient() throws MyException, IOException {
        ensureFastdfsInitialized();
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getTrackerServer();
        StorageServer storageServer = tracker.getStoreStorage(trackerServer);
        return new StorageClient(trackerServer, storageServer);
    }

    private void ensureFastdfsInitialized() throws IOException, MyException {
        if (trackerServers != null && trackerServers.equals(initializedTrackerServers)) {
            return;
        }
        synchronized (fastdfsInitLock) {
            if (trackerServers != null && trackerServers.equals(initializedTrackerServers)) {
                return;
            }
            Path tempConfig = Files.createTempFile("fastdfs-client-", ".properties");
            try {
                Files.writeString(
                        tempConfig,
                        "fastdfs.tracker_servers = " + trackerServers + System.lineSeparator(),
                        StandardCharsets.UTF_8
                );
                ClientGlobal.initByProperties(tempConfig.toString());
                initializedTrackerServers = trackerServers;
            } finally {
                Files.deleteIfExists(tempConfig);
            }
        }
    }
}
