import {QueryNames} from './querynames';
import {UnitComponent} from "../../unit/unit.component";

export class SenseContent {
  lemma: string;
  senseId: string;
  variant: number;

  partOfSpeech;
  grammaticalGender;
  flag;

  fields: Array<Object> = [];
  areas: Array<Object> = [];
  yiddishVariant = 'Default';

  constructor(json: Object, currentYiddishVariant=null) {
    console.log(json);
    this.senseId = json['id'];
    this.variant = json['variant'];
    this.partOfSpeech = json['part_of_speech'];
    this.grammaticalGender = null;
    this.flag = json['lexicon'];

    this.lemma = json['lemma'] + ' ' + this.variant + ' (' + json['domain'] + ')';
    // todo - change
    // if (json['_links']['yiddish']) {
    //   if (currentYiddishVariant && json['Yiddish'][currentYiddishVariant]) {
    //     this.yiddishVariantId = currentYiddishVariant;
    //   } else {
    //     this.yiddishVariantId = 0;
    //   }
    //   this.currentYiddish = json['Yiddish'][this.yiddishVariantId];
    //   this.lemma = this.currentYiddish['Latin spelling'] +  ' ' + this.variant + ' (' + json['Domain'] + ')'
    //     + ' | ' + this.currentYiddish['Yiddish spelling'] + ' | ' +  this.currentYiddish['YIVO spelling'];
    //   this.grammaticalGender = this.currentYiddish['Grammatical gender'];
    //   this.yiddishVariant = this.currentYiddish['Yiddish variant'].replace(/_/g, ' ');
    //   this.setYiddishFields();
    // }

    this.setBasicFields(json);
  }

  private setBasicFields(json): void {
    const fieldNames = ['Domain', 'Lexicon', 'Part of speech'];
    for (const name of fieldNames){
      this.fields.push({name: name, values: [json[name]]});
    }
  }

  private getSearchFieldQuery(name: string, id: number|string) {
    return QueryNames.getQueryString(name, id);
  }

}
