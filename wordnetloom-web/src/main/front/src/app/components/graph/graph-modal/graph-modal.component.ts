import {Component, EventEmitter, Inject, OnDestroy, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material';
import {Subscription} from 'rxjs';

export interface ModalOptions {
  topLabel: string;
  topLabelEmitter: EventEmitter<string>;
}

@Component({
  selector: 'app-graph-modal',
  templateUrl: './graph-modal.component.html',
  styleUrls: ['./graph-modal.component.css']
})
export class GraphModalComponent implements OnInit, OnDestroy {

  selectedUnitName: string = '';
  selectedUnitNameSubscription: Subscription;

  constructor(@Inject(MAT_DIALOG_DATA) public data: ModalOptions) { }

  ngOnInit() {
    console.log(this.data);
    this.selectedUnitName = this.data.topLabel;
    this.selectedUnitNameSubscription = this.data.topLabelEmitter.subscribe(
      label => {
        this.selectedUnitName = label;
      }
    );
  }

  ngOnDestroy() {
    this.selectedUnitNameSubscription.unsubscribe();
  }

}