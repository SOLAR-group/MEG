import pandas as pd
from tqdm import tqdm
from rpy2.robjects import FloatVector
from rpy2.robjects.packages import importr


def vda_all(summary: pd.DataFrame, program):
    MEG = "MEG - F2"
    strategies = summary["strategy"].unique()
    all_vda = {}
    progress = tqdm(enumerate(strategies), total=len(strategies))
    for column_index, column in progress:
        progress.set_description(f"Processing VDA for MEG vs {column}")
        all_vda[column] = vda(
            summary.loc[summary["program"] == program].loc[summary["strategy"] == MEG, "MCC"].to_numpy(),
            summary.loc[summary["program"] == program].loc[summary["strategy"] == column, "MCC"].to_numpy())
    return all_vda


def vda(group_a, group_b):
    effsize = importr('effsize')

    result = effsize.VD_A(FloatVector(group_a), FloatVector(group_b))
    magnitude = result[2]
    estimate = result[3]

    return round(estimate[0], 2), interpret_vda(magnitude[0])


def interpret_vda(vda):
    interpretation = {
        1: "N",
        2: "S",
        3: "M",
        4: "L",
    }
    return interpretation[vda]
