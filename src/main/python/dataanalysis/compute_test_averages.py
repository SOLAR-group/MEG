import os

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import scikit_posthocs as sp
import seaborn as sns
import excel_printer as ep
from scipy import stats
import vda


def read_csvs_from_file(test_file) -> pd.DataFrame:
    """
    Reads the CSV from a file. Returns None if the file does not exist.
    :param test_file: the file containing test results
    :return: a DataFrame with the contents of the file
    """
    if os.path.exists(test_file):
        df = pd.read_csv(test_file)
        if len(df) != 0:
            return df


def compute_summary(df: pd.DataFrame) -> pd.DataFrame:
    """
    Given a DataFrame of a single program, computes the average MCC of each strategy in multiple runs.
    The data is grouped by split.
    The resulting DataFrame is a pivot with the strategy as column, split as index, and average MCC as value.
    :param df: the DataFrame with all results for a given program
    :return: a pivot DataFrame with the strategy as column, split as index, and average MCC as value
    """
    # Sets the default as 0
    df["best"] = 0

    # Finds the list of best strategies in regards to fitness 1
    best_fitness_1 = df.groupby(["program", "run", "strategy"])["FITNESS_1"].transform(max) == df["FITNESS_1"]
    # Finds the list of best strategies in regards to fitness 2
    best_fitness_2 = df.groupby(["program", "run", "strategy"])["FITNESS_2"].transform(max) == df["FITNESS_2"]

    # Computes Euclidean Distance
    df["MAX_FITNESS_1"] = df.groupby(["program", "run", "strategy"])["FITNESS_1"].transform(max)
    df["MAX_FITNESS_2"] = df.groupby(["program", "run", "strategy"])["FITNESS_2"].transform(max)
    df["EUCLIDEAN_DISTANCE"] = np.linalg.norm(
        np.array((df["MAX_FITNESS_2"], df["MAX_FITNESS_1"])) - np.array((df["FITNESS_2"], df["FITNESS_1"])), ord=1,
        axis=0)
    # Find the list of lowest Euclidean Distance solutions
    best_ed = df.groupby(["program", "run", "strategy"])["EUCLIDEAN_DISTANCE"].transform(min) == df[
        "EUCLIDEAN_DISTANCE"]

    # Creates a temporary DataFrame
    best_fitness_1 = df.copy()[best_fitness_1]
    # Creates a temporary DataFrame
    best_fitness_2 = df.copy()[best_fitness_2]
    # Gets a random one
    best_none = df.copy().groupby(["program", "run", "strategy", "best"]).sample(1).reset_index()
    # Creates a temporary DataFrame
    best_ed = df.copy()[best_ed]

    # Sets best as 1
    best_fitness_1["best"] = 1
    # Sets best as 2
    best_fitness_2["best"] = 2
    # Sets best as R
    best_none["best"] = "R"
    # Sets best as R
    best_ed["best"] = "ED"

    # Concatenates both DFs
    df: pd.DataFrame = pd.concat([best_fitness_1, best_fitness_2, best_none, best_ed], axis=0, ignore_index=True)
    # Finds the first best of each group
    df: pd.DataFrame = df.groupby(["program", "run", "strategy", "best"]).first().reset_index()

    # Assigns a new name to the strategy to reflect the best fitness
    df.loc[:, "strategy"] = df["strategy"] + " - F" + df["best"].astype(str)
    # Drops the GA with Fitness 2 or Random, which are useless
    df: pd.DataFrame = df[~df['strategy'].astype(str).str.match(r'((GA|PE).*F[2RED]+|MOEA.*F[1RED]+)')]
    df: pd.DataFrame = df.replace(
        {"strategy": {"DIVACE - F1": "DIVACE"}})

    return df


def create_pivot(df: pd.DataFrame) -> pd.DataFrame:
    # Creates the pivot table
    df = df.pivot(index=["program", "run"], columns="strategy", values="MCC")
    # Rounds MCC to 2 decimal places
    df = df.round(2)
    return df


def generate_boxplot(summary):
    summary = summary.reset_index()
    sns.boxplot(data=summary, x="program", y="MCC", hue="strategy")
    sns.despine(offset=5, trim=True)
    plt.xticks(rotation=45, horizontalalignment='right')
    plt.subplots_adjust(bottom=0.233)
    plt.show()


def get_new_rows(strategy_name: str):
    return [{
        "program": program,
        "strategy": strategy_name,
        "run": run,
        "MCC": base_strategies.loc[
            base_strategies["Program"] == program, base_strategies.columns == strategy_name].to_numpy()[
            0, 0]
    } for run in range(1, num_runs + 1)]


if __name__ == '__main__':
    num_runs = 30
    programs = [
        "activemq-5.0.0-first",
        # "camel-1.4.0-first",
        "derby-10.2.1.6-first",
        "groovy-1_5_7-first",
        "hbase-0.94.0-first",
        "hive-0.9.0-first",
        "jruby-1.1-first",
        "lucene-2.3.0-first",
        "wicket-1.3.0-incubating-beta-1-first",
        "activemq-5.3.0-penultimate",
        # "camel-2.10.0-penultimate",
        "derby-10.3.1.4-penultimate",
        "groovy-1_6_BETA_1-penultimate",
        "hbase-0.95.0-penultimate",
        "hive-0.10.0-penultimate",
        "jruby-1.5.0-penultimate",
        "lucene-3.0.0-penultimate",
        "wicket-1.3.0-beta2-penultimate"]
    strategies = {
        "DISAGREEMENT-MCC": "MEG",
        # "DISAGREEMENT-PRECISION": "MOEA DIS PRE",
        # "WAD-MCC": "MOEA WAD MCC",
        # "ga-MCC": "GA MCC",
        # "ga-PRECISION": "GA PRE",
        # "ga-DISAGREEMENT": "GA DIS",
        # "ga-WAD": "GA WAD",
        "pe-MCC": "DIVACE",
        # "pe-PRECISION": "PE PRE"
    }

    pivot_all: pd.DataFrame = pd.DataFrame()
    summary_all: pd.DataFrame = pd.DataFrame()
    all_p_values = []
    all_pair_wise = {}
    all_vda = {}
    base_strategies = read_csvs_from_file("base-strategies.csv")
    for program in programs:
        data_frame: pd.DataFrame = pd.DataFrame()
        for strategy in strategies:
            real_run = 0
            for run in range(1, num_runs + 1):
                specific_df = read_csvs_from_file(
                    "output-" + strategy + "/" + program + "/TEST_" + str(run) + ".csv")
                if specific_df is not None and len(specific_df.index) > 0:
                    real_run += 1
                    specific_df["program"] = program
                    specific_df["run"] = real_run
                    specific_df["strategy"] = strategies[strategy]
                    data_frame = pd.concat([data_frame, specific_df], axis=0, ignore_index=True)

        summary = compute_summary(data_frame)
        new_rows = [get_new_rows(strategy) for strategy in ["STACKING", "NB", "J48", "KNN", "SMO"]]

        for row in new_rows:
            summary = summary.append(row)

        pivot = create_pivot(summary)
        all_p_values.append(round(stats.kruskal(
            # pivot["MOEA DIS MCC - F1"].to_numpy(),
            pivot["MEG - F2"].to_numpy(),
            # pivot["MOEA DIS PRE - F2"].to_numpy(),
            # pivot["MOEA WAD MCC - F2"].to_numpy(),
            # pivot["MOEA DIS MCC - FR"].to_numpy(),
            # pivot["GA MCC"].to_numpy(),
            pivot["NB"].to_numpy(),
            pivot["J48"].to_numpy(),
            pivot["KNN"].to_numpy(),
            pivot["SMO"].to_numpy(),
            # pivot["GA DIS"].to_numpy(),
            # pivot["GA PRE"].to_numpy(),
            # pivot["GA WAD"].to_numpy(),
            pivot["DIVACE"].to_numpy(),
            # pivot["PE PRE"].to_numpy(),
            pivot["STACKING"].to_numpy(),
            # pivot["SC"].to_numpy(),
            nan_policy="omit").pvalue, 2))
        all_pair_wise[program] = sp.posthoc_mannwhitney(summary, val_col="MCC", group_col="strategy")
        all_vda[program] = vda.vda_all(summary.loc[summary["strategy"].isin(['MEG - F2', 'DIVACE', 'STACKING', 'NB', 'J48', 'KNN', 'SMO'])], program)
        summary_all = pd.concat([summary_all, summary], axis=0)

        pivot.loc["median"] = pivot.median().to_numpy()
        pivot: pd.DataFrame = pivot.iloc[[-1]]
        pivot.index.name = "program"
        pivot.index = [program]
        pivot_all = pd.concat([pivot_all, pivot], axis=0)

    # generate_boxplot(summary_all.loc[summary_all["strategy"].isin(['MEG - F2', 'DIVACE', 'STACKING', 'NB', 'J48', 'KNN', 'SMO'])])
    pivot_all = pivot_all.round(2)
    pivot_all["p-value"] = all_p_values
    pivot_all.loc['mean'] = pivot_all.mean().round(2)
    pivot_all.loc['median'] = pivot_all.median().round(2)
    pivot_all.loc[['mean', 'median'], 'p-value'] = ""
    pivot_all.loc[pivot_all["p-value"] == 0, "p-value"] = "< 0.01"
    pivot_all = pivot_all[['MEG - F2', 'DIVACE', 'STACKING', 'NB', 'J48', 'KNN', 'SMO', 'p-value']]
    ep.print_pretty_excel(all_pair_wise, pivot_all, programs)
    ep.print_pretty_pair_wise(all_pair_wise, programs, pivot_all, all_vda)
    print(pivot_all.to_latex())
