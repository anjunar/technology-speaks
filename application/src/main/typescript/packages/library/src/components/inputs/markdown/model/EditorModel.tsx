import {Basic, Entity} from "../../../../mapper";
import {AbstractEntity} from "../../../../domain/container";
import File from "./File";
import {Root} from "mdast";

@Entity("editor")
export default class EditorModel extends AbstractEntity {

    $type = "editor"

    @Basic()
    files: File[]

    ast: Root

    @Basic()
    get json() {
        return JSON.stringify(this.ast)
    }

    set json(value) {
        this.ast = JSON.parse(value)
    }

}
