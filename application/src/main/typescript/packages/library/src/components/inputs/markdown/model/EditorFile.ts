import {Basic, Entity} from "../../../../mapper";
import {AbstractEntity} from "../../../../domain/container";

@Entity("File")
export default class EditorFile extends AbstractEntity {

    $type = "File"

    @Basic()
    name: string
    @Basic()
    type: string
    @Basic()
    subType: string
    @Basic()
    lastModified: number
    @Basic()
    data: string
}

