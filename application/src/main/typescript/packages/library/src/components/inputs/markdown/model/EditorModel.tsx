import {Basic, Entity} from "../../../../mapper";
import {AbstractEntity} from "../../../../domain/container";
import File from "./File";

@Entity("editor")
export default class EditorModel extends AbstractEntity {

    $type = "editor"

    @Basic()
    files: File[]

    @Basic()
    ast: string
}
