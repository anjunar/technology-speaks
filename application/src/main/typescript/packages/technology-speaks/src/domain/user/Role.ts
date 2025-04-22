import AbstractEntity from "../core/AbstractEntity";
import {Basic, Entity} from "react-ui-simplicity";

@Entity("Role")
export default class Role extends AbstractEntity {

    $type = "Role"

    @Basic()
    name : string

    @Basic()
    description : string

}