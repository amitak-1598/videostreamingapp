package com.stream.app.services.imp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.stream.app.entities.Video;
import com.stream.app.repositories.VideoRepository;
import com.stream.app.services.VideoService;

import jakarta.annotation.PostConstruct;

@Service
public class VideoServiceImpl  implements VideoService{

	@Value("${files.video}")
	String DIR;
	
	@Value("${file.video.hsl}")
	String HSL_DIR;
	
    @Autowired
    VideoRepository videorepository;
	
	@PostConstruct
	public void init() {
		File file = new File(DIR);
		File file1= new File(HSL_DIR);
		
		if(!file1.exists()) {
			file1.mkdir();
		}
		
		if(!file.exists()) {
			file.mkdir();
			System.out.println("Folder Created");
		}else {
			System.out.println("Folder already created");
		}
	}
	
	@Override
	public Video save(Video video, MultipartFile file) {
		 try {
		 String filename = file.getOriginalFilename();
		 String contentType = file.getContentType();
		 InputStream inputstream = file.getInputStream();
		 
		 
		String cleanFileName = StringUtils.cleanPath(filename);
		String cleanFolder = StringUtils.cleanPath(DIR);
		Path path = Paths.get(cleanFolder,cleanFileName);
		System.out.println(contentType);
		 System.out.println(path);
		 
		 Files.copy(inputstream, path,StandardCopyOption.REPLACE_EXISTING);
		 
		 video.setContentType(contentType);
		 video.setFilePath(path.toString());
		 
		 
		Video savedVideo =  videorepository.save(video);
		 processVideo(video.getVideoId());
		 
		return savedVideo;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public Video get(String videoId) {
		Video video = videorepository.findById(videoId).orElseThrow(() -> new RuntimeException("video not found"));
		return video;
	}

	@Override
	public Video getByTitle(String title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Video> getAll() {
		     
		return videorepository.findAll();
	}

	@Override
	public String processVideo(String videoId) {
		 
	           Video video = this.get(videoId);
	            String filePath = video.getFilePath();
		
		// path where to store data;
		
		Path videoPath =  Paths.get(filePath);
//		String output360p = HSL_DIR+videoId+"/360p/";
//		String output720p = HSL_DIR+videoId+"/720p/";
//		String output1080p = HSL_DIR+videoId+"/1080p/";
		
		try {
//		Files.createDirectories(Paths.get(output360p));
//		Files.createDirectories(Paths.get(output720p));		
//		Files.createDirectories(Paths.get(output1080p)); 
			
			
		 Path outputPath = Paths.get(HSL_DIR, videoId);
		      Files.createDirectories(outputPath);
		 
//		      String ffmpegCmd = String.format(
//	                    "ffmpeg -i \"%s\" -c:v libx264 -c:a aac -strict -2 -f hls -hls_time 10 -hls_list_size 0 -hls_segment_filename \"%s/segment_%%3d.ts\"  \"%s/master.m3u8\" ",
//	                    videoPath, outputPath, outputPath
//	            );
		      
		      String ffmpegCmd = String.format(
		    		    "ffmpeg -i \"%s\" -c:v libx264 -c:a aac -strict -2 -f hls -hls_time 10 -hls_list_size 0 -hls_segment_filename \"%s/segment_%%3d.ts\"  \"%s/master.m3u8\" ",
		    		    videoPath.toString().replace("\\", "/"), 
		    		    outputPath.toString().replace("\\", "/"), 
		    		    outputPath.toString().replace("\\", "/")
		    		);

		    		// Check the operating system and adjust the command accordingly
		    		ProcessBuilder processBuilder;
		    		if (System.getProperty("os.name").toLowerCase().contains("win")) {
		    		    // Windows system, use cmd.exe
		    		    processBuilder = new ProcessBuilder("cmd.exe", "/c", ffmpegCmd);
		    		} else {
		    		    // Unix-like system (Linux, MacOS, etc.), use bash
		    		    processBuilder = new ProcessBuilder("/bin/bash", "-c", ffmpegCmd);
		    		}

		    		processBuilder.inheritIO(); // This will direct output and errors to the console

		    		try {
		    		    Process process = processBuilder.start();
		    		    int exitCode = process.waitFor();
		    		    
		    		    if (exitCode != 0) {
		    		        throw new RuntimeException("Video processing failed with exit code: " + exitCode);
		    		    }
		    		    
		    		    System.out.println("Video processing completed successfully!");
		    		} catch (IOException e) {
		    		    e.printStackTrace();
		    		    throw new RuntimeException("Failed to execute video processing command", e);
		    		} catch (InterruptedException e) {
		    		    Thread.currentThread().interrupt();
		    		    throw new RuntimeException("Video processing was interrupted", e);
		    		}
		}
		
	   catch(Exception ex) {
		   ex.printStackTrace();
	   }
		return videoId;
	}
		
		
}