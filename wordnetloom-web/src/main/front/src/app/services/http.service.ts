import { Injectable } from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs/Rx';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/finally';
import 'rxjs/add/operator/catch';
import {SlimLoadingBarService} from 'ng2-slim-loading-bar';

@Injectable()
export class HttpService {
  // apiBase = 'http://www.mocky.io/v2/';
  apiBase = 'http://156.17.135.55:8080/yiddish/api/';

  constructor(private http: Http, private slimLoadingBarService: SlimLoadingBarService) {}

  private handleError() {
    // todo
  }

  private get(uri): Observable<any> {
    this.slimLoadingBarService.start();

    // const headers = new Headers(options);
    // const opt = new RequestOptions({ headers: headers});

    return this.http.get(this.apiBase + uri)
      .map( res => res.json())
      .finally(() => this.slimLoadingBarService.complete() );
  }

  getLexicalUnitDetails(id) {
    // let uris = ['59d4bb9c270000d90607b435', '59d4bee1270000d90607b44e', '59d4beec270000bb0607b44f'];
    // let uri = uris[Math.floor(Math.random() * uris.length)];
    return this.get('sense/' + id);
  }

  getSearchOptions(lemma: String) {
    // todo escape lemma
    return this.get('sense?lemma=' + lemma + '&per_page=1000');
  }
}
