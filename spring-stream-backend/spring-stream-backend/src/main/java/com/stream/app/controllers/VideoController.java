package com.stream.app.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.stream.app.entities.Video;
import com.stream.app.helper.AppConstants;
import com.stream.app.helper.CustomMessage;
import com.stream.app.services.VideoService;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/videos")
public class VideoController {

	private VideoService videoservice;

	public VideoController(VideoService videoService) {
		this.videoservice = videoService;
	}
           // Video Upload
	@PostMapping
	public ResponseEntity<?> create(@RequestParam("file") MultipartFile file,
			@RequestParam("title") String title, @RequestParam("description") String description) {

		Video video = new Video();
		video.setTitle(title);
		video.setDescription(description);
		video.setVideoId(UUID.randomUUID().toString());
	Video savedVideo =	videoservice.save(video, file);
	if(savedVideo!=null) {
		return ResponseEntity.status(HttpStatus.OK).body(video);
		
	}else {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new CustomMessage("Video not uploaded",false));
	}
		

	}

           //Video Stream
	                 // http://localhost:8080/api/v1/videos/stream/id
	@GetMapping("/stream/{videoId}")
	public ResponseEntity<Resource> streama(@PathVariable String videoId){
		System.out.println(videoId);
		System.out.println("Hello");
		System.out.println("Hello");
	//	System.out.println(videoId + " videos id is here");
		Video video = videoservice.get(videoId);
		System.out.println(video);
		String contentType= video.getContentType();
		System.out.println(contentType);
		String filePath = video.getFilePath();
		Resource resource = new FileSystemResource(filePath);
		if(contentType ==null) {
			contentType="application/octet-stream";
			
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.body(resource);
	}
	
	
  @GetMapping("/stream/range/{videoId}")
  public ResponseEntity<Resource> streamVideoRange(@PathVariable String videoId,
		  @RequestHeader(value ="Range",required=false) String range){
	  System.out.println(range);
	  Video video = videoservice.get(videoId);
	  Path path = Paths.get(video.getFilePath());
	  Resource resource = new FileSystemResource(path);
	  String contentType = video.getContentType();
	  if(contentType==null) {
		  contentType="application/octet-stream";
		  
	  }
	  
	  long fileLength = path.toFile().length();
	  
	  if(range==null) {
		  return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
		  
	  }
	  
	  long rangestart;
	  long rangeEnd;
	 String [] ranges= range.replace("bytes=", "").split("-");
	 rangestart=Long.parseLong(ranges[0]);
	 
	 
	   rangeEnd=rangestart+AppConstants.CHUNK_SIZE-1;
	   
	   if(rangeEnd>=fileLength) {
		   rangeEnd=fileLength-1;
	   }
//	 if(ranges.length >1) {
//		 rangeEnd= Long.parseLong(ranges[1]);
//	 }else {
//		 rangeEnd=fileLength-1;
//	 }
//	 
//	 if(rangeEnd>fileLength-1) {
//		 rangeEnd=fileLength-1;
//	 }
	 InputStream inputStream;
	 
	 try {
		 inputStream = Files.newInputStream(path);
		  inputStream.skip(rangestart);
		 
		  
		  System.out.println("Start reading" + rangestart);
		  System.out.println("End readign" + rangeEnd);
		  long contentLength= rangeEnd-rangestart+1;
		  
		  byte[] data = new byte[(int)contentLength];
		  int read = inputStream.read(data,0,data.length);
		  System.out.println("read(number of bytes) : "+read);
		  
			 HttpHeaders headers= new HttpHeaders();
			 headers.add("Content-Range","bytes "+rangestart+"-"+rangeEnd+"/"+fileLength);
			 headers.add("Cache-Control", "no-cache,no-store,must-revalidate");
			 headers.add("Pragma","no-cache");
			 headers.add("Expires", "0");
			 headers.add("X-Content-Type-Options", "nosniff");
			 headers.setContentLength(contentLength);
			 return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(headers)
					 .contentType(MediaType.parseMediaType(contentType))
					 .body(new ByteArrayResource(data));
		 
	 }catch(IOException ex) {
		 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	 }
	
	 
  }
  
  
  @Value("${file.video.hsl}")
	private String HSL_DIR;
	
	@GetMapping("/{videoId}/master.m3u8")
	public ResponseEntity<Resource> serverMasterFile(
		@PathVariable String videoId){
		
	Path path =	Paths.get(HSL_DIR,videoId,"master.m3u8");
	
	System.out.println(path);

	
	if(!Files.exists(path)) {
		return new  ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	Resource resource = new FileSystemResource(path);
	return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE,"application/vnd.apple.mpegurl")
			.body(resource);

	}
	
	
	@GetMapping("/{videoId}/{segment}.ts")
	public ResponseEntity<Resource> serveSegments(@PathVariable String videoId, 
			@PathVariable String segment){
		Path path =	Paths.get(HSL_DIR,videoId,segment+".ts");
		if(!Files.exists(path)) {
			return new  ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Resource resource = new FileSystemResource(path);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE,"video/mp2t")
				.body(resource);
	}
	

	@GetMapping
	public List<Video> getAll(){
		return videoservice.getAll();
	}
	
	
	
}
