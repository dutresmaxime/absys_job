import { Component, OnInit } from '@angular/core';
import {User} from "../class/user";
import {ApiService} from "../services/api.service";
import {MessageService} from "primeng/api";
import {UserService} from "../services/user.service";
import {WebsocketService} from "../services/websocket.service";

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {
  users: User[];

  constructor(
    private userService: UserService,
    private messageService: MessageService,
    private webSocket: WebsocketService
  ) { }

  async ngOnInit() {
    await this.loadUsers();
    await this.loadWebSocket();
  }

  async loadUsers() {
    this.users = await this.userService.findAll();
  }

  async findByJobThenCountry() {
    this.users = await this.userService.findByJobThenCountry();
  }
  /**
   * Auto reload the list when a new user is registered
   * FIXME : Not working
   */
  async loadWebSocket() {
    await this.webSocket.connect();
    // update user on state change
    this.webSocket.subscribe('/workflow/states', (user) => {
      this.loadUsers();
    })
  }

  async onApprouved(id: any) {
    try {
      await this.userService.workflow(id);
      this.messageService.add({severity: 'success', summary: 'Workflow', detail: 'User has been updated'});
      await this.loadUsers();
    } catch (e) {
      this.messageService.add({severity: 'error', summary: 'Workflow', detail: 'Unable to update user'});
    }
  }
}
