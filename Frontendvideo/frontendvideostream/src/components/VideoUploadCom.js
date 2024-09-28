import React from 'react'
import {Button, Card,ClipboardWithIconText,Label,TextInput,Textarea,Progress,Alert} from "flowbite-react"
import { useState,useRef,useEffect } from 'react'
import axios from 'axios';








function videotrigger(){
  alert("new video ")
}

const VideoUploadCom = () => {

useEffect(()=>{
  document.title="Upload Video"
})



 
  const fileresethandle = useRef(null);
  const [selectedFile,setSelectedFile]=useState(null);
  const [alerts,setAlerts]=useState(false);
  const [meta,setMeta] = useState({
    title:"",
    description:"",
  });
  const [progress,setProgress] = useState(0);
  const[uploading,setUploading]=useState(false);
  const[message,setMessage]=useState("");

  const textfieldStyle={
    backgroundColor:"grey"
   }

  function handleFileChange(event){
    console.log(event.target.files[0]); 
    setSelectedFile(event.target.files[0]);
  }

  function formFieldChange(event){
    // console.log(event.target.name)
    // console.log(event.target.value)
    setMeta({
      ...meta,
      [event.target.name]:event.target.value
    })
  }

  function handleForm(formEvent){
   formEvent.preventDefault();
   if(!selectedFile){
    alert("Select File!!");
    return;
   }
    // submit file to server 
    saveVideoToServer(selectedFile,meta);
  }

function resetForm(){

   setMeta({
    title:"",
    description:"",
   });
   setSelectedFile(null);
   setUploading(false);
   console.log(meta);
   console.log(selectedFile);
   console.log(uploading);
   fileresethandle.current.value="";
   
  // setMessage("");
}




  async function  saveVideoToServer(Video,videoMetaData){
   setUploading(true);
   
   // API call
   try{

    let formData=new FormData()
   formData.append("title",videoMetaData.title);
   formData.append("description",videoMetaData.description);
   formData.append("file",selectedFile);


         let response =  await axios.post('http://localhost:8080/api/v1/videos',formData,{
            headers: {
              'Content-Type': 'multipart/form-data'
          },
          onUploadProgress: (progressEvent) => {
            const progress= Math.round((progressEvent.loaded*100)/progressEvent.total);
            setProgress(progress);
            console.log(progress);
          }
      });
          console.log(response);
          setProgress(0);
    setAlerts(true);  
      setMessage("File Uploaded ");
     setUploading(false);
     setTimeout(()=>{
setAlerts(false)
     },2000)
     resetForm()
   }catch(error){
    console.log(error);
    setAlerts(true); 
    setMessage("Error in uploading File");
    setUploading(false);
   }
  }

  return (
    <div className="text-white">

      <Card className="flex flex-col items-center bg-slate-800" >
        <h1>Upload Videos</h1>
        <div>
        <form  noValidate className=" flex flex-col space-y-6" onSubmit={handleForm} >

           <div>
            <div className="mb-2 block">
            <Label htmlFor="file-upload" value="Video Title" color={ClipboardWithIconText}/>
            </div>
            <TextInput value ={meta.title} onChange={formFieldChange} name="title" placeholder='Enter Title' />
           </div>
  

          <div className="max-w-md">
            <div className="mb-2 block">
              <Label htmlFor="comment" value="Video Description" color={ClipboardWithIconText}/>
            </div>
            <Textarea value={meta.description} onChange={formFieldChange} name = "description" id="comment" placeholder="write video description..." required rows={4} />
          </div>



       <div className="flex item-center space-x-5 justify-center">
    <div className="shrink-0">
      <img className="h-16 w-16 object-cover " src="/images/video-posting.png" onClick={videotrigger}/>
    </div>
    <label className="block">
      <span className="sr-only">Choose profile photo</span>
      <input 
      name="file"
      onChange={handleFileChange}
      type="file" className="block w-full text-sm text-slate-500
        file:mr-4 file:py-2 file:px-4
        file:rounded-full file:border-0
        file:text-sm file:font-semibold
        file:bg-violet-50 file:text-violet-700
        hover:file:bg-violet-100
      "   ref={fileresethandle}/>
    </label>
    </div>


   <div className="">
    {uploading && (
       <Progress
       color="green"
       progress={progress} 
       textLabel="Uploading" 
       size={"lg"} 
       labelProgress 
       labelText />
    )}
   
   </div>

   {
    alerts?<>
     <div className="">
    <Alert color="success" onDismiss={()=> setAlerts(false)}>
     <span className="font-medium">Success alert!</span> {message}
       </Alert>
   </div>
    </>:<>
    </>
   }
  


    <div className="flex justify-center">
      <Button disabled={uploading} type="submit">Submit</Button>
    </div>
  </form>  
  </div>

  {/* <div className="flex justify-center">
   <Button onClick={handleForm}>Upload</Button>  

   
    </div> */}
  
      </Card>  
    </div>
  )
}

export default VideoUploadCom
