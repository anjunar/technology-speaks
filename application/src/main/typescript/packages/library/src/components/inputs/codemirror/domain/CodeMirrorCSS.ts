import {AbstractCodeMirrorFile} from "./AbstractCodeMirrorFile";
import Basic from "../../../../mapper/annotations/Basic";
import {Entity} from "../../../../mapper";
import {CodeMirrorContent} from "./CodeMirrorContent";

@Entity("CodeMirrorCSS")
export class CodeMirrorCSS extends AbstractCodeMirrorFile implements CodeMirrorContent {

    $type = "CodeMirrorCSS"

    @Basic()
    content : string

}