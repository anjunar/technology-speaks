import {AbstractContainerNode, AbstractNode} from "../../core/TreeNode";
import Entity from "../../../../../mapper/annotations/Entity";
import Basic from "../../../../../mapper/annotations/Basic";

@Entity("TextNode")
export class TableCellNode extends AbstractContainerNode<AbstractNode> {

    $type = "TableCellNode"

    @Basic()
    readonly children: AbstractNode[];

    constructor(children: AbstractNode[]) {
        super(children);
    }
}

@Entity("TextNode")
export class TableRowNode extends AbstractContainerNode<TableCellNode> {

    $type = "TableRowNode"

    @Basic()
    readonly children: TableCellNode[];

    constructor(children: TableCellNode[]) {
        super(children);
    }
}

@Entity("TextNode")
export class TableNode extends AbstractContainerNode<TableRowNode> {

    $type = "TableNode"

    @Basic()
    rows : number = 1

    @Basic()
    cols : number = 2

    @Basic()
    readonly children: TableRowNode[];

    constructor(children: TableRowNode[]) {
        super(children);
    }

}