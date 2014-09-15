/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.magnum.mobilecloud.video;

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.lang.Void;

import javax.servlet.http.HttpServletResponse;

import org.magnum.mobilecloud.video.repository.*;
import org.magnum.mobilecloud.video.auth.User;
import org.magnum.mobilecloud.video.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;

@Controller
public class VideoSvc {
	
	/**
	 * You will need to create one or more Spring controllers to fulfill the
	 * requirements of the assignment. If you use this file, please rename it
	 * to something other than "AnEmptyController"
	 * 
	 * 
		 ________  ________  ________  ________          ___       ___  ___  ________  ___  __       
		|\   ____\|\   __  \|\   __  \|\   ___ \        |\  \     |\  \|\  \|\   ____\|\  \|\  \     
		\ \  \___|\ \  \|\  \ \  \|\  \ \  \_|\ \       \ \  \    \ \  \\\  \ \  \___|\ \  \/  /|_   
		 \ \  \  __\ \  \\\  \ \  \\\  \ \  \ \\ \       \ \  \    \ \  \\\  \ \  \    \ \   ___  \  
		  \ \  \|\  \ \  \\\  \ \  \\\  \ \  \_\\ \       \ \  \____\ \  \\\  \ \  \____\ \  \\ \  \ 
		   \ \_______\ \_______\ \_______\ \_______\       \ \_______\ \_______\ \_______\ \__\\ \__\
		    \|_______|\|_______|\|_______|\|_______|        \|_______|\|_______|\|_______|\|__| \|__|
                                                                                                                                                                                                                                                                        
	 * 
	 */
	
	@Autowired
	private VideoRepository videoRepository;


	@RequestMapping(value="/video", method= RequestMethod.POST )
	public @ResponseBody Video addVideo(
			@RequestBody Video v,
			HttpServletResponse response) {
		v.setLikes(0);
	
		response.setStatus(HttpServletResponse.SC_OK);
		return videoRepository.save(v);
	}
	
	
	@RequestMapping(value="/video", method= RequestMethod.GET )
	public @ResponseBody Collection<Video> getVideoList() {
		return Lists.newArrayList(videoRepository.findAll());
	}

	
	@RequestMapping(value="/video/{id}" , method= RequestMethod.GET )
	public @ResponseBody Video getVideoById(
			@PathVariable("id") long id,
			HttpServletResponse response) {
		if (videoRepository.findOne(id)!=null){
		  return videoRepository.findOne(id);
		}else{
		 response.setStatus(HttpStatus.NOT_FOUND.value());
		 return null;
		}
	}
	
	@RequestMapping(value="/video/search/findByName" , method= RequestMethod.GET )
	public @ResponseBody Collection<Video> findByTitle(
			 @RequestParam("title") String title,
			HttpServletResponse response) {
		if (videoRepository.findByName(title)!=null){
		  return videoRepository.findByName(title);
		}else{
		 response.setStatus(HttpStatus.NOT_FOUND.value());
		 return null;
		}
	}
	
	
	@RequestMapping(value="/video/search/findByDurationLessThan", method= RequestMethod.GET )
	public @ResponseBody Collection<Video> findByDurationLessThan(
			 @RequestParam("duration") long duration,
			HttpServletResponse response) {
		if (videoRepository.findByDurationLessThan(duration)!=null){
		  return videoRepository.findByDurationLessThan(duration);
		}else{
		 response.setStatus(HttpStatus.NOT_FOUND.value());
		 return null;
		}
	}
	
	
	
	@RequestMapping(value="/video/{id}/like" , method=RequestMethod.POST )
	public @ResponseBody void likeVideo(
			 @PathVariable("id") long id,
			 HttpServletResponse response,
			 Principal p
			) {
		Video v = videoRepository.findOne(id);
		
		if (v==null){
		 response.setStatus(HttpStatus.NOT_FOUND.value());
		}else{
			Set<String> likes = v.getLikesUsers();
			
			if (likes.contains(p.getName())){
			  response.setStatus(HttpStatus.BAD_REQUEST.value());
			}else{
			  likes.add(p.getName());
			  v.setLikesUsers(likes);
			  v.setLikes(likes.size());
			  videoRepository.save(v);
			}
		}
	}

	
	@RequestMapping(value="/video/{id}/unlike" , method= RequestMethod.POST )
	public void unlikeVideo(
			 @PathVariable("id") long id,
			 Principal p,
			 HttpServletResponse response
			) {
		Video v = videoRepository.findOne(id);
		
		if (v==null){
		 response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		
		Set<String> likes = v.getLikesUsers();
		
		if (likes.contains(p.getName())){
		  likes.remove(p.getName());
		  v.setLikesUsers(likes);
		  v.setLikes(likes.size());
		  videoRepository.save(v);
		}else{
		  response.setStatus(HttpStatus.BAD_REQUEST.value());
		}
	}
	
	@RequestMapping(value="/video/{id}/likedby" , method= RequestMethod.GET )
	public @ResponseBody Collection<String> getUsersWhoLikedVideo(
			@PathVariable("id") long id,
			 HttpServletResponse response
			) {
		Video v = videoRepository.findOne(id);
		
		if (v==null){
		 response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		
		Set<String> likes = v.getLikesUsers();
		
		if (likes != null ){
			Collection <String> videos = new HashSet<String>(); 
			videos.addAll(likes);
			return videos;
		}else{
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return null;
		}
	}

	
	@RequestMapping(value="/go",method=RequestMethod.GET)
	public @ResponseBody String goodLuck(){
		return "Good Luck!";
	}

}
