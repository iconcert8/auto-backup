package com.gyeom.domain;

import com.gyeom.model.DirectoryPath;
import com.gyeom.model.FileVO;

import java.util.List;

public interface FileRepository {

    /**
     * The file upload to destination
     *
     * @param dest   Destination folder.
     * @param fileVO Source file
     * @return
     */
    boolean upload(DirectoryPath dest, FileVO fileVO);

    /**
     * The file remove at path(destination)
     *
     * @param fileVO Target file
     * @return
     */
    boolean remove(FileVO fileVO);

    /**
     * List of files on the destination
     *
     * @param dest Destination folder.
     * @return
     */
    List<FileVO> list(DirectoryPath dest);

}
