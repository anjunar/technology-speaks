import AbstractEntity from "../core/AbstractEntity";
import {Basic, Entity} from "react-ui-simplicity";

@Entity("Identity")
export default class Identity extends AbstractEntity {

    $type = "Identity"

    @Basic()
    enabled : boolean

    @Basic()
    deleted : boolean

}