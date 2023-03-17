function [matrix] = ReadDataFromFile(file, r, c)
    rows = r;
    cols = c;
    
    fileID = fopen(file, 'r');
    formatSpec = '%f';
    A = fscanf(fileID, formatSpec);
    fclose(fileID);
    
    vals = zeros(rows, cols);
    
    x = 1;
    for i = 1 : rows
        for j = 1: cols
            vals(i,j) = A(x);
            x = x + 1;
        end
    end
    
    matrix = vals;
    return;

end