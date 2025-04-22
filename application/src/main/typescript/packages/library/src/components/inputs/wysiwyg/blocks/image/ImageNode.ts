import {AbstractNode} from "../../core/TreeNode";
import Basic from "../../../../../mapper/annotations/Basic";

export class ImageNode extends AbstractNode {

    @Basic()
    src : string = ""

    @Basic()
    aspectRatio : number = 1

    @Basic()
    width : number = 360

    @Basic()
    height : number = 360
}