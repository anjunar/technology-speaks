import {Basic, Entity, AbstractEntity} from "react-ui-simplicity";
import User from "./User";

@Entity("group")
export default class Group extends AbstractEntity {

    $type = "group"

    @Basic()
    name : string

    @Basic()
    description : string

    @Basic()
    users : User[]


}