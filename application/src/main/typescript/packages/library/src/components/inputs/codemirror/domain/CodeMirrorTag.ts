import {Basic, Entity} from "../../../../mapper";
import {AbstractEntity} from "../../../../domain/container";

@Entity("CodeMirrorTag")
export default class CodeMirrorTag extends AbstractEntity {

    $type = "CodeMirrorTag"

    @Basic()
    name : string

}