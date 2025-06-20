import {AbstractCodeMirrorFile} from "./AbstractCodeMirrorFile";
import Basic from "../../../../mapper/annotations/Basic";
import {Entity} from "../../../../mapper";
import {CodeMirrorContent} from "./CodeMirrorContent";

@Entity("CodeMirrorTS")
export class CodeMirrorTS extends AbstractCodeMirrorFile implements CodeMirrorContent {

    $type = "CodeMirrorTS"

    @Basic()
    content : string

    @Basic()
    transpiled : string

    @Basic()
    sourceMap : string

}