package com.superspytx.ab.handlers.chat;

import com.superspytx.ab.abs.EventAction;
import com.superspytx.ab.abs.Handler;
import com.superspytx.ab.abs.PI;
import com.superspytx.ab.settings.Lang;
import com.superspytx.ab.settings.Settings;
import com.superspytx.ab.tils.Tils;
import com.superspytx.ab.workflow.GD;

public class CaptchaHandler implements Handler {
	
	@Override
	public boolean run(EventAction info) {
		if (!Settings.captchaEnabled) return false;
		PI pli = GD.getPI(info.player);
		if (pli.cp_haspuzzle) {
			if (pli.cp_puzzle.overboard())
				return true;
			else {
				pli.cp_idle = System.currentTimeMillis();
				if (!pli.cp_puzzle.checkAnswer(info.message)) {
					String wrong = pli.cp_puzzle.getAttempts() == 1 ? Lang.CAPONELEFT.toString() : Lang.CAPATTEMPTSLEFT.toString().replace("%a", String.valueOf(pli.cp_puzzle.getAttempts()));
					info.player.sendMessage(Lang.PREFIX.toString() + Lang.CAPWRONG.toString().replace("%w", wrong.toString()));
				} else {
					pli.cp_haspuzzle = false;
					pli.cp_solvedpuzzle = true;
					pli.resetSpamData();
					info.player.chat(pli.cs_lsm);
					info.player.sendMessage(Lang.PREFIX.toString() + Lang.CAPRIGHT.toString());
				}
			}
		}
		return false;
	}
	
	@Override
	public void performActions(EventAction info) {
		Tils.kickPlayer(info.player, Lang.CAPTCHAKICK.toString());
	}
	
}
