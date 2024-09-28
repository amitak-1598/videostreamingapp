import logo from './logo.svg';
import './App.css';
import VideoUploadCom from './components/VideoUploadCom';
import VideoPlayer from './components/VideoPlayer';
import { useState } from 'react';
import { Button, TextInput } from 'flowbite-react';




function App() {

 const [videoId,setVideoId] = useState("ca4f4a3b-9015-475b-88f5-dc16224e5166");
 const[fieldValue,setFieldValue]=useState(null);


function playVideo(videoId){
  setVideoId(videoId);
}


  return (
    <>
   
    <div className=" flex flex-col items-center space-y-9  justify-center py-9">
         <h1 className="text-3xl font-bold text-grey-700 dark:text-gray-100">Video Steaming App</h1>
  
  
        <div className="flex w-full mt-14 justify-around">
        <div>
        <h1 className='text-white text-center mt-2' >Playing Video</h1>
       
        {/* <video style={{width:500,height:500}} 
         src={`http://localhost:8080/api/v1/videos/stream/range/${videoId}`} 
        // src={`localhost:8080/api/v1/videos/50646b91-dd92-4ded-94fb-263f6612fcba/master.m3u8`}
        controls> </video> */}
        {/* <video
    id="my-video"
    class="video-js"
    controls
    preload="auto"
    width="640"
    height="264"
    poster="MY_VIDEO_POSTER.jpg"
    data-setup="{}"
  >
    <source src={`http://localhost:8080/api/v1/videos/stream/range/${videoId}`} type="video/mp4" />
    <p class="vjs-no-js">
      To view this video please enable JavaScript, and consider upgrading to a
      web browser that
      <a href="https://videojs.com/html5-video-support/" target="_blank"
        >supports HTML5 video</a
      >
    </p>
  </video> */}


 <div>
  <VideoPlayer src={`http://localhost:8080/api/v1/videos/${videoId}/master.m3u8`} 
  ></VideoPlayer>
 </div>




       </div>
       
  <VideoUploadCom/>
        </div>
  

<div className="my-4 flex space-x-4">
  <TextInput onChange={(e)=>{setFieldValue(e.target.value)}}
  placeholder="Enter video id here" name="video_id_field" />
  <Button onClick={()=>{
    setVideoId(fieldValue);
  }}>Play</Button>
</div>


    </div>
    </>
  );
}

export default App;
