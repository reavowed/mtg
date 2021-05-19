import _ from "lodash";
import {createElement} from "preact";
import {useCallback, useContext} from "preact/hooks";
import GameState from "../../GameState";
import {addClass} from "../../utils/element-utils";
import {useRefWithEventHandler} from "../../utils/hook-utils";
import HorizontalCenter from "../layout/HorizontalCenter";
import Card from "./Card";
import CardRow from "./CardRow";

export default function Hand({cards, ...props}) {
    return <CardRow cards={cards || useContext(GameState).hand} {...props} />;
}
