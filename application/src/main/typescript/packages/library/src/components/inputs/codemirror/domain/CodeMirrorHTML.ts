import {AbstractCodeMirrorFile} from "./AbstractCodeMirrorFile";
import Basic from "../../../../mapper/annotations/Basic";
import {Entity} from "../../../../mapper";
import {CodeMirrorContent} from "./CodeMirrorContent";

@Entity("CodeMirrorHTML")
export class CodeMirrorHTML extends AbstractCodeMirrorFile implements CodeMirrorContent{

    $type = "CodeMirrorHTML"

    @Basic()
    content : string

}