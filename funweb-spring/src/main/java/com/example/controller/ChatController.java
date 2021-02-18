package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.chat.ChatRoom;
import com.example.chat.ChatRoomRepository;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/chat/*")
@Slf4j
public class ChatController {
	
	@Autowired
	private ChatRoomRepository chatRoomRepository;
	
	@GetMapping("/list")
	public String list(Model model) {
		List<ChatRoom> roomList = chatRoomRepository.getAllRooms();
		model.addAttribute("roomList", roomList);
		return "chat/chatList";
	}
	
	@GetMapping("/new")
	public String makeRoom() {
		return "chat/newRoom";
	}
	
	@PostMapping("/new")
	public String makeRoom(String title) {
		ChatRoom chatRoom = chatRoomRepository.createChatRoom(title);
		//return "redirect:/chat/list"; // 방목록으로 리다이렉트 시키기
		return "redirect:/chat/room/" + chatRoom.getRoomId(); // 채팅방 참여 경로로 리다이렉트 시키기
	}
	
	@GetMapping("/room/{roomId}")
	public String room(@PathVariable("roomId") String roomId, Model model) {
		ChatRoom chatRoom = chatRoomRepository.getRoomById(roomId);
		model.addAttribute("room", chatRoom);
		return "chat/roomChat";
	}
	
	
	//========= simple chat app =========
	@GetMapping("/simpleChat")
	public void simpleChat() {
//		return "chat/simpleChat";
	}
	//===================================
}


