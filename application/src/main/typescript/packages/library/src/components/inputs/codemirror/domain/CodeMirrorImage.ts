import {AbstractCodeMirrorFile} from "./AbstractCodeMirrorFile";
import Basic from "../../../../mapper/annotations/Basic";
import {Entity} from "../../../../mapper";

@Entity("CodeMirrorImage")
export class CodeMirrorImage extends AbstractCodeMirrorFile {

    $type = "CodeMirrorImage"

    @Basic()
    data : string
    
    @Basic()   
    contentType : string

}