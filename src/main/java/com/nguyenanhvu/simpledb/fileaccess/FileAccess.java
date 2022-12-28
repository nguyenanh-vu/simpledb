package com.nguyenanhvu.simpledb.fileaccess;

import java.io.IOException;
import java.nio.file.Path;

public interface FileAccess {
	public byte[] read(Path path) throws IOException;
	
	public byte[] read(Path path, Long id);
	
	public void read(Path path, byte[] dest);
	
	public byte[] readChained(Path path, Long startLine, Long startId);
	
	public void readLine(Path path, Long id, byte[] dest) throws IOException;
	
	public void write(Path path, Long id, byte[] src);
	
	public void writeChained(Path path, Long startLine, Long startId, byte[] src);
}
