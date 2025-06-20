import {AbstractEntity} from "../../../../domain/container";
import Entity from "../../../../mapper/annotations/Entity";
import {AbstractCodeMirrorFile} from "./AbstractCodeMirrorFile";
import Basic from "../../../../mapper/annotations/Basic";

@Entity("CodeMirrorWorkspace")
export class CodeMirrorWorkspace extends AbstractEntity {

    $type = "CodeMirrorWorkspace"

    @Basic()
    open : AbstractCodeMirrorFile[] = []

}