import xlsxwriter as xls

import vda


def is_equal_to_best(program, strategy, max_strategy, all_pair_wise):
    if strategy in max_strategy:
        return True
    else:
        pair_wise = all_pair_wise[program].loc[strategy, max_strategy]
        if pair_wise[pair_wise > 0.05].any():
            return True
        else:
            return False


def print_pretty_excel(all_pair_wise, pivot_all, programs):
    with xls.Workbook('output.xlsx') as workbook:
        excel_sheet = workbook.add_worksheet("summary")
        bold_fmt = workbook.add_format({'bold': True})
        best_fmt = workbook.add_format({'bg_color': '#E0FFFF'})
        excel_sheet.write(0, 0, "Program", bold_fmt)
        for column_index, column in enumerate(pivot_all.columns):
            excel_sheet.write(0, column_index + 1, column, bold_fmt)

        for program_index, program in enumerate(programs):
            row_index = program_index + 1
            excel_sheet.write(row_index, 0, program, bold_fmt)
            program_row = pivot_all.iloc[[program_index]]
            max_row = program_row.loc[:, program_row.columns != "p-value"].max(axis=1)
            max_strategy = []
            for column_index, column in enumerate(program_row.columns):
                if column != "p-value":
                    strategy = pivot_all.columns[column_index]
                    if program_row[column][0] == max_row[0]:
                        max_strategy.append(strategy)

            for column_index, column in enumerate(program_row.columns):
                if column == "p-value":
                    excel_sheet.write(row_index, column_index + 1, program_row[column][0])
                else:
                    strategy = pivot_all.columns[column_index]
                    if is_equal_to_best(program, strategy, max_strategy, all_pair_wise):
                        excel_sheet.write(row_index, column_index + 1, program_row[column][0], best_fmt)
                    else:
                        excel_sheet.write(row_index, column_index + 1, program_row[column][0])


def print_pretty_pair_wise(all_pair_wise, programs, pivot_all, all_vda):
    with xls.Workbook('output-p-values.xlsx') as workbook:
        excel_sheet = workbook.add_worksheet("summary")
        bold_fmt = workbook.add_format({'bold': True})
        better_fmt = workbook.add_format({'bg_color': '#E0FFFF'})
        worse_fmt = workbook.add_format({'bg_color': '#FFE4E1'})
        excel_sheet.write(0, 0, "Program", bold_fmt)
        MEG = "MEG - F2"
        strategies = pivot_all.columns[:-1].drop(MEG)
        for column_index, column in enumerate(strategies):
            excel_sheet.write(0, column_index + 1, column, bold_fmt)

        count_best = {}
        count_equal = {}
        count_worse = {}
        for program_index, program in enumerate(programs):
            row_index = program_index + 1
            excel_sheet.write(row_index, 0, program, bold_fmt)
            program_row = all_pair_wise[program]
            program_vda = all_vda[program]

            for column_index, column in enumerate(strategies):
                p_value = round(program_row.loc[MEG, column], 3)
                estimate, magnitude = program_vda[column]
                if p_value < 0.05:
                    if p_value < 0.01:
                        p_value = "< 0.01"
                    if estimate < 0.5:
                        excel_sheet.write(row_index, column_index + 1, f"{p_value} / {estimate} ({magnitude})", worse_fmt)
                        count_worse[column] = count_worse.get(column, 0) + 1
                    else:
                        excel_sheet.write(row_index, column_index + 1, f"{p_value} / {estimate} ({magnitude})", better_fmt)
                        count_best[column] = count_best.get(column, 0) + 1
                else:
                    excel_sheet.write(row_index, column_index + 1, f"{p_value} / {estimate} ({magnitude})")
                    count_equal[column] = count_equal.get(column, 0) + 1

        excel_sheet.write(row_index + 1, 0, "MEG best")
        excel_sheet.write(row_index + 2, 0, "MEG equal")
        excel_sheet.write(row_index + 3, 0, "MEG worse")
        for column_index, column in enumerate(strategies):
            excel_sheet.write(row_index + 1, column_index + 1, count_best[column])
            excel_sheet.write(row_index + 2, column_index + 1, count_equal[column])
            excel_sheet.write(row_index + 3, column_index + 1, count_worse[column])
