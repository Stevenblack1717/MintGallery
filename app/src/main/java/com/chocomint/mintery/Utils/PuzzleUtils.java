package com.chocomint.mintery.Utils;

import com.chocomint.mintery.CollageLayout.slant.OneSlantLayout;
import com.chocomint.mintery.CollageLayout.slant.SlantLayoutHelper;
import com.chocomint.mintery.CollageLayout.slant.ThreeSlantLayout;
import com.chocomint.mintery.CollageLayout.slant.TwoSlantLayout;
import com.chocomint.mintery.CollageLayout.straight.EightStraightLayout;
import com.chocomint.mintery.CollageLayout.straight.FiveStraightLayout;
import com.chocomint.mintery.CollageLayout.straight.FourStraightLayout;
import com.chocomint.mintery.CollageLayout.straight.NineStraightLayout;
import com.chocomint.mintery.CollageLayout.straight.OneStraightLayout;
import com.chocomint.mintery.CollageLayout.straight.SevenStraightLayout;
import com.chocomint.mintery.CollageLayout.straight.SixStraightLayout;
import com.chocomint.mintery.CollageLayout.straight.StraightLayoutHelper;
import com.chocomint.mintery.CollageLayout.straight.ThreeStraightLayout;
import com.chocomint.mintery.CollageLayout.straight.TwoStraightLayout;
import com.xiaopo.flying.puzzle.PuzzleLayout;

import java.util.ArrayList;
import java.util.List;

public class PuzzleUtils {
  private static final String TAG = "PuzzleUtils";

  private PuzzleUtils() {
  }

  public static PuzzleLayout getPuzzleLayout(int type, int borderSize, int themeId) {
    if (type == 0) {
      switch (borderSize) {
        case 1:
          return new OneSlantLayout(themeId);
        case 2:
          return new TwoSlantLayout(themeId);
        case 3:
          return new ThreeSlantLayout(themeId);
        default:
          return new OneSlantLayout(themeId);
      }
    } else {
      switch (borderSize) {
        case 1:
          return new OneStraightLayout(themeId);
        case 2:
          return new TwoStraightLayout(themeId);
        case 3:
          return new ThreeStraightLayout(themeId);
        case 4:
          return new FourStraightLayout(themeId);
        case 5:
          return new FiveStraightLayout(themeId);
        case 6:
          return new SixStraightLayout(themeId);
        case 7:
          return new SevenStraightLayout(themeId);
        case 8:
          return new EightStraightLayout(themeId);
        case 9:
          return new NineStraightLayout(themeId);
        default:
          return new OneStraightLayout(themeId);
      }
    }
  }

  public static List<PuzzleLayout> getAllPuzzleLayouts() {
    List<PuzzleLayout> puzzleLayouts = new ArrayList<>();
    //slant layout
    puzzleLayouts.addAll(SlantLayoutHelper.getAllThemeLayout(2));
    puzzleLayouts.addAll(SlantLayoutHelper.getAllThemeLayout(3));

    // straight layout
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(2));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(3));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(4));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(5));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(6));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(7));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(8));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(9));
    return puzzleLayouts;
  }

  public static List<PuzzleLayout> getPuzzleLayouts(int pieceCount) {
    List<PuzzleLayout> puzzleLayouts = new ArrayList<>();
    puzzleLayouts.addAll(SlantLayoutHelper.getAllThemeLayout(pieceCount));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(pieceCount));
    return puzzleLayouts;
  }
}
