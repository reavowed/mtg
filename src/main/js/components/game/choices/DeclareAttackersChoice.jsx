import _ from "lodash";
import {useCallback, useContext, useEffect, useState} from "preact/hooks";
import ActionManager from "../../../contexts/ActionManager";
import GameState from "../../../contexts/GameState";
import {getPlural} from "../../../utils/word-helpers";
import BannerText from "../../layout/BannerText";
import HorizontalCenter from "../../layout/HorizontalCenter";
import DecisionButton from "../DecisionButton";

export default function DeclareAttackersChoice() {
    const gameState = useContext(GameState);
    const actionManager = useContext(ActionManager);
    const possibleAttackers = gameState.currentChoice.details.possibleAttackers;
    const [selectedAttackers, setSelectedAttackers] = useState([]);

    const onObjectClick = useCallback((objectId) => {
        if (_.includes(possibleAttackers, objectId)) {
            if (_.includes(selectedAttackers, objectId)) {
                setSelectedAttackers(_.filter(selectedAttackers, a => a !== objectId));
            } else {
                setSelectedAttackers([...selectedAttackers, objectId]);
            }
        }
    }, [gameState, selectedAttackers]);
    const getClasses = useCallback((objectId) => {
        if (_.includes(selectedAttackers, objectId)) {
            return ["attacking"];
        } else {
            return [];
        }
    }, [selectedAttackers]);
    useEffect(() => {
        actionManager.setActionHandler(() => onObjectClick);
        actionManager.setClassGetter(() => getClasses);
        return () => {
            actionManager.setActionHandler(null);
            actionManager.setClassGetter(null);
        }
    }, [gameState, selectedAttackers]);
    return <div>
        <BannerText as="p">Declare Attackers</BannerText>
        <HorizontalCenter>
            <DecisionButton optionToChoose={selectedAttackers.join(" ")}>{getPlural(selectedAttackers.length, "Attacker", "Attackers")}</DecisionButton>
        </HorizontalCenter>
    </div>;
}
