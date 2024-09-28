import React , {useEffect,useRef} from 'react'
import videojs from 'video.js'
import Hls from 'hls.js'
import "video.js/dist/video-js.css"


function VideoPlayer({src}) {

const videoRef = useRef(null)
const playerREf=useRef(null)


useEffect(()=> {
    
    playerREf.current=videojs(videoRef.current,{
        controls:true,
        autoplay:true,
        muted:true,
        preload:"auto"
    });

   if(Hls.isSupported()){
    const hls = new Hls()
    hls.loadSource(src)
    hls.attachMedia(videoRef.current)
    hls.on(Hls.Events.MANIFEST_PARSED,()=>{
        videoRef.current.play();
    });
   }else if(videoRef.current.canPlayType("application/vnd.apple.mpegurl")){
    videoRef.current.src=src
    videoRef.current.addEventListner("canplay",()=>{
        videoRef.current.play();
    });
   }else{
    console.log("video format not supported");
   }



},[src])
  return (
    <div>
           <div data-vjs-player>
<video ref ={videoRef}
style={{
    width:"300%",
    height:"500px",
}}
className="video-js vjs-control-bar"
></video>
           </div>
    </div>
  );
}

export default VideoPlayer
