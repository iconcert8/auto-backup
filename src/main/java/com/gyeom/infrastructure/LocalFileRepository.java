package com.gyeom.infrastructure;

import com.gyeom.domain.FileRepository;
import com.gyeom.model.DirectoryPath;
import com.gyeom.model.FileVO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LocalFileRepository implements FileRepository {

    @Override
    public boolean upload(DirectoryPath dest, FileVO fileVO) {
        return false;
    }

    @Override
    public boolean remove(FileVO fileVO) {
        return removeFile(fileVO.getFullName());
    }

    @Override
    public List<FileVO> list(DirectoryPath dest) {
        return listFile(dest.toString()).stream()
                .map(f ->
                        {
                            try {
                                return new FileVO(
                                        f.getName(),
                                        DirectoryPath.of(f),
                                        Files.readAttributes(Path.of(f.getPath()), BasicFileAttributes.class).creationTime().toInstant()
                                );
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                .collect(Collectors.toList());
    }

    public List<File> listFile(String directory) {
        return List.of(Objects.requireNonNull(new File(directory).listFiles()));
    }

    public boolean removeFile(String path) {
        return new File(path).delete();
    }
}
