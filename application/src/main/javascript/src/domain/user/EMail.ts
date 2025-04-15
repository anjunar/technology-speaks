import AbstractEntity from "../core/AbstractEntity";
import {Basic, Entity} from "react-ui-simplicity";

@Entity("EMail")
export default class EMail extends AbstractEntity {

    $type = "EMail"

    @Basic()
    value : string

}