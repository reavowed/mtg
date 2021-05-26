import _ from "lodash";
import {useCallback, useContext, useEffect, useRef, useState} from "preact/hooks";
import ActionManager from "../../../contexts/ActionManager";
import CanvasManager from "../../../contexts/CanvasManager";
import GameState from "../../../contexts/GameState";
import {getPlural} from "../../../utils/word-helpers";
import BannerText from "../../layout/BannerText";
import HorizontalCenter from "../../layout/HorizontalCenter";
import DecisionButton from "../DecisionButton";
import $ from "jquery";

export default function DeclareBlockersChoice() {
    const gameState = useContext(GameState);
    const actionManager = useContext(ActionManager);
    const canvasManager = useContext(CanvasManager);

    const [selectedBlocks, setSelectedBlocks] = useState([]);
    const [currentPossibleBlocker, setCurrentPossibleBlocker] = useState(null);
    const attackers = gameState.currentChoice.details.attackers;
    const possibleBlockers = gameState.currentChoice.details.possibleBlockers;

    // Update blocker lines when blockers change
    useEffect(() => {
        canvasManager.setLines([...selectedBlocks]);
    }, [selectedBlocks]);

    // Document click handling - reset in-progress blocker choice
    const onDocumentClick = useCallback((event) => {
        if (event.originalEvent.fromHandler === this) return;
        setCurrentPossibleBlocker(null);
    }, []);
    useEffect(() => {
        $(document).on("click", onDocumentClick);
        return () => $(document).off("click", onDocumentClick);
    }, [onDocumentClick]);

    // Object click handling
    const onObjectClick = useCallback((objectId, event) => {
        if (currentPossibleBlocker && currentPossibleBlocker.objectId === objectId) {
            // Cancel possible blocker choice
            event.originalEvent.fromHandler = this;
            setCurrentPossibleBlocker(null);
        } else if (currentPossibleBlocker && _.includes(attackers, objectId)) {
            // Complete possible blocker choice
            event.originalEvent.fromHandler = this
            setSelectedBlocks(prev => [...prev, [currentPossibleBlocker.objectId, objectId]]);
            setCurrentPossibleBlocker(null);
        } else if (_.some(selectedBlocks, x => x[0] === objectId)) {
            // Cancel previously-made blocker choice
            setSelectedBlocks(prev => [..._.filter(prev, x => x[0] !== objectId)]);
        } else if (_.includes(possibleBlockers, objectId)) {
            // Start possible blocker choice
            event.originalEvent.fromHandler = this;
            setCurrentPossibleBlocker({objectId, event});
        }
    }, [possibleBlockers, currentPossibleBlocker, selectedBlocks]);

    const getClasses = useCallback((objectId) => {
        const isPossibleBlocker = (currentPossibleBlocker && currentPossibleBlocker.objectId === objectId);
        const isBlocker = _.some(selectedBlocks, x => x[0] === objectId);
        if (isPossibleBlocker || isBlocker) {
            return ["blocking"];
        } else {
            return [];
        }
    }, [currentPossibleBlocker, selectedBlocks]);
    useEffect(() => {
        actionManager.setActionHandler(() => onObjectClick);
        actionManager.setClassGetter(() => getClasses);
        return () => {
            actionManager.setActionHandler(null);
            actionManager.setClassGetter(null);
        }
    }, [onObjectClick, getClasses]);
    useEffect(() => {
        if (currentPossibleBlocker) {
            canvasManager.setTemporaryLineStart(currentPossibleBlocker);
            return () => canvasManager.setTemporaryLineStart(null);
        }
    }, [currentPossibleBlocker]);

    const option = _.map(selectedBlocks, ([a, b]) => `${a} ${b}`).join(" ");

    return <div>
        <BannerText as="p">Declare Blockers</BannerText>
        <HorizontalCenter>
            <DecisionButton optionToChoose={option}>{getPlural(selectedBlocks.length, "Blocker", "Blockers")}</DecisionButton>
        </HorizontalCenter>
    </div>;
}
